package problem.helper;


import common.STATUS;
import component.resource.MachineResource;
import component.resource.Resource;
import component.variable.Variable;
import component.variable.impl.Task;
import representation.Solution;

import java.util.List;
import java.util.stream.Collectors;

public class MachineResourceConflictHelper extends ResourceConflictHelper {

    @Override
    public double evaluateResourceConflict(Solution solution, int numberOfResource) {
        double assignment = 0;
        List<Variable> variables = solution.getVariables();
        List<Task> tasks = joinOrders(variables);


        for (int i = 0; i < numberOfResource; i++) {
            double resourceAssignmentCount = countResourceAssignedTimes(tasks, i);
            double rjAssignment;
            if (resourceAssignmentCount > 0) {
                rjAssignment = countResourceConflict(tasks, i)/resourceAssignmentCount;
                assignment += rjAssignment;
            }
        }
        return assignment/numberOfResource;
    }

    @Override
    public double countResourceAssignedTimes(List<Task> tasks, int resourceId) {
        double count = 0;
        for (Task variable: tasks) {
            List<MachineResource> machineResources = ((Task) variable).getRequiredMachinesResources()
                    .stream()
                    .filter(resource -> resource.getId() == resourceId)
                    .collect(Collectors.toList());
            if (!machineResources.isEmpty() && machineResources.get(0).getStatus() == STATUS.ASSIGNED) {
                count++;
            }
        }
        return count;
    }

    @Override
    public double countResourceConflict(List<Task> tasks, int resourceId) {
        double count = 0;
        int variableSize = tasks.size();
        int[] conflictMap = new int[variableSize];
        for (int i = 0; i < variableSize - 1; i++) {
            for (int j = i + 1; j < variableSize; j++) {
                boolean isConflict = isResourceConflict(tasks.get(i), tasks.get(j), resourceId);
                if (isConflict) {
                    conflictMap[i] = 1;
                    conflictMap[j] = 1;
                }
            }
        }

        for (int i = 0; i < variableSize; i++) {
            if (conflictMap[i] == 1)
                count++;
        }
        return count;
    }

    @Override
    public boolean isResourceConflict(Task var1, Task var2, int resourceId) {
        Resource resource1 = isResourceUsefulToTask(var1, resourceId);
        Resource resource2 = isResourceUsefulToTask(var2, resourceId);
        if (resource1 != null && resource2 != null) {
            if (resource1.getStatus() == resource2.getStatus() && resource1.getStatus() == STATUS.ASSIGNED) {
                double start1 = ((Task) var1).getStart(); double end1 = ((Task) var1).getDuration() + start1;
                double start2 = var2.getStart(); double end2 = var2.getDuration() + start2;
                if(start1 == start2) {
                    if(end1 <= end2 || end2 <= end1 ) {
                        return true;
                    }
                } else if (start1 < start2) {
                    if (start2 < end1) {
                        if(end1 <= end2 || end2 <= end1)
                            return true;
                    }
                } else if (start2 < start1) {
                    if (start1 < end2) {
                        if (end1 <= end2 || end2 <= end1)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private Resource isResourceUsefulToTask(Task task, int resourceId) {
        List<MachineResource> machineResources = task.getRequiredMachinesResources().stream()
                .filter(machineResource -> machineResource.getId() == resourceId).collect(Collectors.toList());
        return machineResources.size() > 0 ? machineResources.get(0) : null;
    }
}
