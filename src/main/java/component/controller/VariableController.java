package component.controller;

import component.Variable;

import java.util.List;
import java.util.Map;

public abstract class VariableController {

    public abstract List<Variable> createVariables(Map<Object, Object> parameters, double k);

}
