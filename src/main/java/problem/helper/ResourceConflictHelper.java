package problem.helper;

import common.STATUS;
import component.resource.Resource;
import component.variable.Variable;
import component.variable.impl.Task;
import solution.Solution;

import java.util.List;

public abstract class ResourceConflictHelper {

    public abstract double evaluateResource(Solution solution, int numberOfResource);

    public abstract double countResourceAssignedTimes(List<Variable> variables, int resourceId);

    public abstract double countResourceConflict(List<Variable> variables, int resourceId);

    public abstract boolean isResourceConflict(Variable var1, Variable var2, int resourceId);

}
