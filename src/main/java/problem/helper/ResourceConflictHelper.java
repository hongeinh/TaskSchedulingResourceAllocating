package problem.helper;

import component.variable.Variable;
import component.variable.impl.Task;
import representation.Solution;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceConflictHelper {

    public abstract double evaluateResourceConflict(Solution solution, int numberOfResource);

    public abstract double countResourceAssignedTimes(List<Task> variables, int resourceId);

    public abstract double countResourceConflict(List<Task> variables, int resourceId);

    public abstract boolean isResourceConflict(Task var1, Task var2, int resourceId);

    protected List<Task> joinOrders(List<Variable> orders) {
        List<Task> tasks = new ArrayList<>();
        for (Variable order : orders) {
            List<Task> orderVariables = (List<Task>) order.getValue();
            tasks.addAll(orderVariables);
        }
        return tasks;
    }
}
