/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
 * LIG Laboratory (http://www.liglab.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package fr.liglab.adele.cube.agent.defaults.resolver;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.cmf.ManagedElement;
import fr.liglab.adele.cube.agent.cmf.Property;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Characteristic;
import fr.liglab.adele.cube.archetype.Element;
import fr.liglab.adele.cube.archetype.Objective;

import java.util.Stack;

/**
 *
 * A CSP containing at most binary constraints may be viewed as a constraint graph which can
 * guide a problem solver. Each graph node corresponds to a problem variable, and both unary
 * and binary constraints are represented by labelled, directed arcs.
 *
 *
 * Constraints can be:
 * - absolute constraints: violation of which rules out a potential solution.
 * - preference constraints: indicating which solutions are preferred (not yet implemented!).
 *
 * Preference constraints can often be encoded as costs on individual variable assignments,
 * for example, assigning an afternoon slot for Prof. X costs 2 points against the overall
 * objective function, whereas a morning slot costs 1.
 *
 * CSP with preferences can be solved using optimization search methods, either path-based
 * or local.
 *
 * Constraint Graph is commutative. This mean when assigning values to variables, we reach
 * the same partial assignment, regardless of order.
 *
 * We use a 'backtracking search'. This is a depth-first search that chooses values for one
 * variable at a time and backtracks when a variable has no legal values left to assign.
 *
 * tree decomposition of the constraint graph into a set of connected subproblems.
 *   Each subproblem is solved independently, and the resulting solutions are then combined.
 *   Like most divide-and-conquer algorithms, this works well if no subproblem is too large.
 *   Requirements:
 *   - Every variable in the original problem appears in at least one of the subproblems.
 *   - If two variables are connected by a constraint in the original problem, they must
 *     appear together (along with the constraint) in at least one of the subproblems.
 *   - If a variable appears in two subproblems in the tree, it must appear in every
 *     subproblem along the path connecting those subproblems.
 *
 *
 * Author: debbabi
 * Date: 4/27/13
 * Time: 11:45 PM
 */
public class ResolutionGraph {

    /**
     * Root variable.
     */
    private Variable root;


    private CubeAgent agent;

    public ResolutionGraph(CubeAgent agent) {
        this.agent = agent;
    }

    public void setRoot(Variable var) {
        this.root = var;
    }

    public boolean resolve() {
        System.out.println("************** resolve ****************");
        if (root != null) {
           preProcessing();
            for (Constraint o : root.getObjectiveConstraints()) {
                resolveObjective(o);
            }
        }
        return true;
    }

    private boolean resolveObjective(Constraint objective) {
        if (objective.isBinaryConstraint()) {
            // resolve and apply binary objective here..
            // 1. find value for the object variable
            String uuid = find(objective.getObjectVariable());
            while (uuid != null) {
                objective.applyObjective();
                if (evaluateValue(objective.getObjectVariable())==true) {
                    return true;
                }
                objective.cancelObjective();
                uuid = find(objective.getObjectVariable());
            }

            /*
             if no value is find for the object (related) variable, no solution is found.
             TODO: In the future, we can put it in the unresolved objectives, and try again
             after some moments.
             */
             System.out.println("[WARNING] The objective '"+objective.getName()+"' could not be resolved!");
             return false;

        }  else {
            // apply unary objective here..
            // TODO: c.applyObjective()
            objective.applyObjective(); // always?!
            return true;
        }
    }

    /**
     *
     * @return UUID
     */
    private String find(Variable v) {
        // start the finding process at the last known step.
        switch (v.findStep) {
            case 0: {
                // apply characteristic constraints on the current variable to minimize the research domain.
                applyCharacteristics(v);
                // go directly to step 1 below
                v.findStep++;
            }
            case 1: {
                // find using the current configuration.

                String uuid = findUsingCharacteristics(v);
                while (uuid != null) {
                    v.values.push(uuid);
                    //v.addAlreadyTestedValue(uuid);
                    if (evaluateValue(v) == true) {
                        return uuid;
                    } else {
                        // add to already tested values
                        // find again using the same technique until no value will be returned!
                        uuid = findUsingCharacteristics(v);
                    }
                }
                v.findStep++;
            }
            case 2: {
                // find from related constraints.
                //String uuid = findUsingConstraints(v);
                for (Constraint bc : v.getBinaryConstraints()) {
                    Variable ov = bc.getObjectVariable();
                    if (ov != null) {
                        String uuid = null;
                        if (ov.getValue() != null) {
                            uuid = bc.find();
                            if (uuid != null) {
                                v.values.push(uuid);
                                return uuid;
                            }
                        }
                        uuid = find(ov);
                        if (uuid != null) {
                            v.values.push(uuid);
                            return uuid;
                        }
                    }
                }
                v.findStep++;
            }
            case 3: {
                // create an instance with the given characteristics.
                String uuid = createInstance(v);
                while (uuid != null) {
                    v.values.push(uuid);
                    //v.addAlreadyTestedValue(uuid);
                    if (evaluateValue(v) == true) {
                        return uuid;
                    } else {
                        // create another instance, but with different configuration!
                        uuid = createInstance(v);
                    }
                }
            }
           default: {
                // no solution was found!
                return null;
            }
        }
    }

    /**
     * Evaluated the found value uuid against the variable's v constraints.
     *
     * @param v
     * @return
     */
    private boolean evaluateValue(Variable v) {

        return true;
    }

    /**
     * Apply characteristics specified by unary constraints to the given variable
     * to minimize the search domain space.
     * @param v
     */
    private void applyCharacteristics(Variable v) {
        for (Constraint c : v.getUnaryConstraints()) {
            c.applyCharacteristic();
        }
    }

    private String findUsingCharacteristics(Variable v) {
        if (v.getCubeAgent() != null) {
            if (v.getCubeAgent().equalsIgnoreCase(this.agent.getUri())) {
                // local search
                for (ManagedElement me : this.agent.getRuntimeModel().getManagedElements(ManagedElement.VALID)) {
                    if (v.values.contains(me.getUUID())) {
                        // bypass if already tested!
                        continue;
                    }
                    boolean equiv = true;
                    for (Property p : v.getProperties()) {
                        String pme = me.getProperty(p.getName());
                        if (pme != null && pme.equalsIgnoreCase(p.getValue())) {
                            continue;
                        } else {
                            equiv = false;
                            break;
                        }
                    }
                    if (equiv == true) {
                         return me.getUri();
                    }
                }
            } else {
                // TODO: remote
                String agent = v.getCubeAgent();
                // communicate...

            }
        }
        return null;
    }

    private String findUsingConstraints(Variable v) {
        for (Constraint bc : v.getBinaryConstraints()) {
            Variable ov = bc.getObjectVariable();
            if (ov != null) {
                if (ov.getValue() != null) {

                }
                String uuid = find(ov);
                while (uuid != null) {
                    // found, we use it to find our subject variable.
                    String result = bc.find();

                    uuid = find(ov);
                }
            }
        }
        return null;
    }

    private String createInstance(Variable v) {
        return null;
    }

    /**
     * Find Objective constraints to be applied to the current root variable instance.
     */
    private void preProcessing() {
        Archetype archetype = this.agent.getArchetype();
        if (archetype != null) {
            // find all objectives for this element
            System.out.println("--------- looking for objective constraints ---------");
            for (Objective obj : archetype.getObjectives()) {
                if (obj.getSubject().getName().equalsIgnoreCase(root.getName()) &&
                        obj.getNamespace().equalsIgnoreCase(root.getNamespace())) {
                    System.out.println("** checking obj: " + obj.getName());
                    if (evaluateSubject(root, obj.getSubject()) == true) {
                        Constraint c = addConstraint(root, obj);
                        System.out.println("** adding objective constraint: " + c.getName());
                        Variable objVar = c.getObjectVariable();
                        if (objVar != null && objVar.isPrimitive() == false) {
                            System.out.println("----------------- building the path of the constraint graph ----------");
                            buildObject(objVar, (Element)obj.getObject());
                            System.out.println("----------------------------------------------------------------------");
                        }
                    }
                }
            }

        }
    }

    private boolean evaluateSubject(Variable var, Element e) {
        Stack<Constraint> stack = new Stack<Constraint>();
        if (evaluateSubject(var, e, stack) == false) {
            // vider stack
            while (stack.isEmpty() == false) {
                Constraint c = stack.pop();
                var.removeConstraint(c);
                System.out.println("** removing characteristic constraint for subject var: " + c.getName());
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean evaluateSubject(Variable var, Element e, Stack<Constraint> stack) {
        if (var.values.size() > 0) {
            /* if only one predicate is false, return false*/
            for (Characteristic car : e.getUnaryCharacteristics()) {
                Constraint c = addConstraint(var, car);
                stack.push(c);
                System.out.println("** adding characteristic constraint for subject var: " + c.getName());
                if (c.check() == false)
                    return false;
                else
                    System.out.println("** checked ok");
            }
            for (Characteristic car : e.getBinaryCharacteristics()) {
                Constraint c = addConstraint(var, car);
                System.out.println("** adding characteristic constraint for subject var: " + c.getName());
                stack.push(c);
                c.init(); // initialize object variable.
                if (c.getObjectVariable().values.size() == 0)
                    // no initialization value from the subject var.
                    return false;
                if (evaluateSubject(c.getObjectVariable(), (Element) car.getObject()) == false)
                    return false;
            }
            return true;
        }
        return false;
    }

    private void buildObject(Variable var, Element e) {

        for (Characteristic car : e.getUnaryCharacteristics()) {
            Constraint c = addConstraint(var, car);
            System.out.println("** adding unary characteristic constraint for subject var: " + c.getName());
        }
        for (Characteristic car : e.getBinaryCharacteristics()) {
            Constraint c = addConstraint(var, car);
            System.out.println("** adding binary characteristic constraint for subject var: " + c.getName());
            buildObject(c.getObjectVariable(), (Element)car.getObject());
        }
        for (Objective car : e.getUnaryObjectives()) {
            Constraint c = addConstraint(var, car);
            System.out.println("** adding unary characteristic(obj) constraint for subject var: " + c.getName());
        }
        for (Objective car : e.getBinaryObjectives()) {
            Constraint c = addConstraint(var, car);
            System.out.println("** adding binary characteristic(obj) constraint for subject var: " + c.getName());
            buildObject(c.getObjectVariable(), (Element)car.getObject());
        }

    }

    private Constraint addConstraint(Variable var, Objective objective) {
        Variable objvar = null;
        if (objective.getObject() instanceof Element) {
            // Binary
            Element obj = (Element)objective.getObject();
            objvar = new Variable(this.agent, obj.getNamespace(), obj.getName());
        } else {
            // Unary
            objvar = new Variable(this.agent, objective.getObject().toString());
        }
        Constraint c = new Constraint(var, objective.getNamespace(), objective.getName(), objvar, true);
        return c;
    }

    private Constraint addConstraint(Variable var, Characteristic car) {
        Variable objvar;
        if (car.getObject() instanceof Element) {
            // Binary
            Element obj = (Element)car.getObject();
            objvar = new Variable(this.agent, obj.getNamespace(), obj.getName());
        } else {
            // Unary
            objvar = new Variable(this.agent, car.getObject().toString());
        }
        Constraint c = new Constraint(var, car.getNamespace(), car.getName(), objvar);
        return c;
    }

    public String show() {
        String out = "";
        if (root != null) {
            out += root.getName();
        }
        return out;
    }
}
