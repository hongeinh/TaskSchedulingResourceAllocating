package component.controller;

import component.variable.Variable;

import java.util.List;
import java.util.Map;

public abstract class VariableController {

    public abstract List<Variable> setupVariables(Map<Object, Object> parameters, double k);

}
