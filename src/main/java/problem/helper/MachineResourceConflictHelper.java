package problem.helper;


import common.STATUS;
import component.resource.MachineResource;
import component.resource.Resource;
import component.variable.Variable;
import component.variable.impl.Task;
import solution.Solution;

import javax.crypto.Mac;
import java.util.List;
import java.util.stream.Collectors;

public class MachineResourceConflictHelper extends ResourceConflictHelper {

    @Override
    public double evaluateResource(Solution solution, int numberOfResource) {
        double assignment = 0;

        for (int i = 0; i < numberOfResource; i++) {
            double resourceAssignmentCount = countResourceAssignedTimes(solution.getVariables(), i);
            double rjAssignment;
            if (resourceAssignmentCount > 0) {
                rjAssignment = countResourceConflict(solution.getVariables(), i)/resourceAssignmentCount;
                assignment += rjAssignment;
            }
        }
        return assignment/numberOfResource;
    }

    @Override
    public double countResourceAssignedTimes(List<Variable> variables, int resourceId) {
        double count = 0;
        for (Variable variable: variables) {
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
    public double countResourceConflict(List<Variable> variables, int resourceId) {
        double count = 0;
        int variableSize = variables.size();
        int[] conflictMap = new int[variableSize];
        for (int i = 0; i < variableSize; i++) {
            for (int j = i + 1; j < variableSize; j++) {
                boolean isConflict = isResourceConflict(variables.get(i), variables.get(j), resourceId);
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
    public boolean isResourceConflict(Variable var1, Variable var2, int resourceId) {
        Resource resource1 = ((Task) var1).getRequiredMachinesResources().get(resourceId);
        Resource resource2 = ((Task) var2).getRequiredMachinesResources().get(resourceId);
        if (resource1.getStatus() == resource2.getStatus() && resource1.getStatus() == STATUS.ASSIGNED) {
            double start1 = ((Task) var1).getStart(); double end1 = ((Task) var1).getDuration() + start1;
            double start2 = ((Task) var2).getStart(); double end2 = ((Task) var2).getDuration() + start2;
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
        return false;
    }

}
