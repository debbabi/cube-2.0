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

import fr.liglab.adele.cube.agent.*;
import fr.liglab.adele.cube.agent.defaults.ResolverImpl;
import fr.liglab.adele.cube.metamodel.*;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.Characteristic;
import fr.liglab.adele.cube.archetype.Element;
import fr.liglab.adele.cube.archetype.Objective;

import java.util.Properties;
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

    private Resolver resolver;
    private CubeAgent agent;


    public ResolutionGraph(Resolver resolver) {
        this.resolver = resolver;
        this.agent = resolver.getCubeAgent();
    }

    public void setRoot(Variable var) {
        this.root = var;
    }

    public Variable getRoot() {
        return this.root;
    }

    /**
     * Resolve the Constraints Graph.
     * @return
     */
    public boolean resolve() {
        //System.out.println("************** resolve ****************");
        if (root != null) {
            // create the resolution graph from the archetype.
            preProcessing();
            print();
            // For each objective constraint related to the root variable,
            // we try to resolve the specified constraints.
            for (Constraint o : root.getObjectiveConstraints()) {
                if (resolveObjective(o) == false) {
                    // TODO: cancel applied constraints!!!!
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Find Objective constraints to be applied to the current root variable instance.
     */
    private void preProcessing() {
        Archetype archetype = this.agent.getArchetype();
        if (archetype != null) {
            // find all objectives for this element
            for (Objective obj : archetype.getObjectives()) {
                /*
                 * if the subject of the objective constraint has the same metamodel as the root variable
                 */
                if (obj.getSubject().getName().equalsIgnoreCase(root.getName()) &&
                        obj.getNamespace().equalsIgnoreCase(root.getNamespace())) {

                    // we check if all the descriptions in the archetype for this subject variable are met for the
                    // current instance
                    if (evaluateSubject(root, obj.getSubject())) {
                        Constraint c = addConstraint(root, obj);
                        info("the objective constraint '"+c.getName()+"' should be resolved");
                        Variable objVar = c.getObjectVariable();

                        /*
                         * if it is a binary constraint, we construct the graph's path.
                         */
                        if (objVar != null && !objVar.isPrimitive()) {
                            buildObject(objVar, (Element)obj.getObject());
                        }
                    }
                }
            }

        }
    }

    /**
     * Resolve an objective constraint
     * @param objective
     * @return
     */
    private boolean resolveObjective(Constraint objective) {
        if (objective.isBinaryConstraint()) {
            // resolve and apply binary objective here..
            // 1. find value for the object variable
            info("trying to find value for the object variable of the objective constraint '" + objective.getName() + "'");

            String uuid = null;
            if (objective.getResolutionStrategy() == Constraint.FIND) {
                uuid = find(objective.getObjectVariable());
            } else if (objective.getResolutionStrategy() == Constraint.FIND_OR_CREATE) {
                uuid = findOrCreate(objective.getObjectVariable());
            } else if (objective.getResolutionStrategy() == Constraint.CREATE) {
                uuid = create(objective.getObjectVariable());
            }

            while (uuid != null) {
                info("find: " + uuid);
                objective.performObjective(this.agent);
                if (evaluateValue(objective.getObjectVariable())) {
                    return true;
                }
                objective.cancelObjective(this.agent);

                // find new value
                if (objective.getResolutionStrategy() == Constraint.FIND) {
                    uuid = find(objective.getObjectVariable());
                } else if (objective.getResolutionStrategy() == Constraint.FIND_OR_CREATE) {
                    uuid = findOrCreate(objective.getObjectVariable());
                } else if (objective.getResolutionStrategy() == Constraint.CREATE) {
                    uuid = create(objective.getObjectVariable());
                }
            }

            /*
             if no value is find for the object (related) variable, no solution is found.
             TODO: In the future, we can put it in the unresolved objectives, and try again
             after some moments.
             */
             info("The objective '" + objective.getName() + "' could not be resolved!");
             return false;

        }  else {
            // apply unary objective here..
            // TODO: c.performObjective()
            objective.performObjective(this.agent); // always?!
            return true;
        }
    }



    /**
     * Find a value for a variable.
     * @return UUID
     */
    private String find(Variable v) {
        info("finding value for '" + v.getName()+"' ...");
        // start the finding process at the last known step.
        switch (v.findStep) {
            case 0: {
                info("step 0 : applying characteristics..");
                // apply characteristic constraints on the current variable to minimize the research domain.
                applyUnaryDescriptions(v);
                // go directly to step 1 below
                v.findStep++;
            }
            case 1: {
                info("step 1 : find using characteristics..");
                // find using the current configuration.
                String uuid = findUsingCharacteristics(v);
                while (uuid != null) {
                    v.values.push(uuid);
                    //v.addAlreadyTestedValue(uuid);
                    //if (evaluateValue(v)) {
                        return uuid;
                    //} else {
                        // add to already tested values
                        // find again using the same technique until no value will be returned!
                    //    uuid = findUsingCharacteristics(v);
                    //}
                }
                info("step 1 : not found using characteristics!");
                v.findStep++;
            }
            case 2: {
                // find from related constraints.
                //String uuid = findUsingConstraints(v);
                info("step 2 : find using binary constraints..");
                for (Constraint bc : v.getBinaryConstraints()) {
                    Variable ov = bc.getObjectVariable();
                    if (ov != null) {

                        if (ov.getValue() == null) {
                            String result = find(ov);
                            while (result != null) {

                                String uuid = bc.find(agent);
                                if (uuid != null) {
                                    v.values.push(uuid);
                                    return uuid;
                                }
                                result = find(ov);
                            }
                            // problem, we cannot find object variable!
                            info("step 2 : not found using binary constraints!");
                            v.findStep++;
                            //return null;

                        } else {
                            String result = ov.getValue().toString();
                            while (result != null) {

                                String uuid = bc.find(agent);
                                if (uuid != null) {
                                    v.values.push(uuid);
                                    return uuid;
                                }
                                result = find(ov);
                            }
                            // problem, we cannot find object variable!
                            info("step 2 : not found using binary constraints!");
                            v.findStep++;
                            //return null;
                        }

                    }
                }
                info("step 2 : not found using binary constraints!");
                v.findStep++;
            }
           default: {
                // no solution was found!
                return null;
            }
        }
    }

    /**
     * Find a value for a variable.
     * @return UUID
     */
    private String findOrCreate(Variable v) {
        info("finding value for '" + v.getName()+"' ...");
        // start the finding process at the last known step.
        switch (v.findStep) {
            case 0: {
                info("step 0 : applying characteristics..");
                // apply characteristic constraints on the current variable to minimize the research domain.
                applyUnaryDescriptions(v);
                // go directly to step 1 below
                v.findStep++;
            }
            case 1: {
                info("step 1 : find using characteristics..");
                // find using the current configuration.
                String uuid = findUsingCharacteristics(v);
                while (uuid != null) {
                    v.values.push(uuid);
                    //v.addAlreadyTestedValue(uuid);
                    if (evaluateValue(v)) {
                        return uuid;
                    } else {
                        // add to already tested values
                        // find again using the same technique until no value will be returned!
                        uuid = findUsingCharacteristics(v);
                    }
                }
                info("step 1 : not found using characteristics!");
                v.findStep++;
            }
            case 2: {
                // find from related constraints.
                //String uuid = findUsingConstraints(v);
                info("step 2 : find using binary constraints..");
                for (Constraint bc : v.getBinaryConstraints()) {
                    Variable ov = bc.getObjectVariable();
                    if (ov != null) {

                        if (ov.getValue() == null) {
                            String result = find(ov);
                            while (result != null) {
                                String uuid = bc.find(agent);
                                if (uuid != null) {
                                    v.values.push(uuid);
                                    return uuid;
                                }
                                result = find(ov);
                            }
                            // problem, we cannot find object variable!
                            info("step 2 : not found using binary constraints!");
                            v.findStep++;
                            //return null;

                        } else {
                            String result = ov.getValue().toString();
                            while (result != null) {
                                String uuid = bc.find(agent);
                                if (uuid != null) {
                                    v.values.push(uuid);
                                    return uuid;
                                }
                                result = find(ov);
                            }
                            // problem, we cannot find object variable!
                            info("step 2 : not found using binary constraints!");
                            v.findStep++;
                            //return null;
                        }

                    }
                }
                info("step 2 : not found using binary constraints!");
                v.findStep++;
            }
            case 3: {
                // create an instance with the given characteristics.
                info("step 3 : creating new instance..");

                //apply binary constraints

                for (Constraint c : v.getBinaryConstraints()) {
                    if (c.getObjectVariable() != null && c.getObjectVariable().getValue() != null) {
                        c.applyDescription(agent);
                        c.getObjectVariable().removeValue();
                        String uuid = createInstance(v);
                        v.values.push(uuid);
                        return uuid;
                    }
                }

                String uuid = createInstance(v);
                v.values.push(uuid);
                v.findStep++;
                info("step 3 : creation problem!");
                return uuid;

            }
            default: {
                // no solution was found!
                return null;
            }
        }
    }

    /**
     * Find a value for a variable.
     * @return UUID
     */
    private String create(Variable v) {
        info("finding value for '" + v.getName()+"' ...");
        // start the finding process at the last known step.
        switch (v.findStep) {
            case 0: {
                info("step 0 : applying characteristics..");
                // apply characteristic constraints on the current variable to minimize the research domain.
                applyUnaryDescriptions(v);
                // go directly to step 1 below
                v.findStep++;
            }
            case 1: {
                // create an instance with the given characteristics.
                info("step 3 : creating new instance..");

                String uuid = createInstance(v);
                while (uuid != null) {
                    v.values.push(uuid);
                    //v.addAlreadyTestedValue(uuid);
                    //if (evaluateValue(v) == true) {
                    v.findStep++;
                    return uuid;
                    //} else {
                    // create another instance, but with different configuration!
                    //    uuid = createInstance(v);
                    //}
                }
                info("step 3 : creation problem!");
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
        info("checking object variable: " + v.getName());
        for (Constraint c : v.getConstraints()) {
            info("checking constraint: " + c.getName());

            if (c.check(agent) == false)  {
                info("constraint '"+c.getName()+"' returns FALSE!");
                return false;
            }
            info("constraint '" + c.getName()+"' TRUE");
        }
        return true;
    }

    /**
     * Apply characteristics specified by unary constraints to the given variable
     * to minimize the search domain space.
     * @param v
     */
    private void applyUnaryDescriptions(Variable v) {
        System.out.println("/////////////////////// applyUnaryDescriptions: " + v.getName());
        for (Constraint c : v.getUnaryConstraints()) {
            System.out.println("/////////////////////// unary constraint: " + c.getName());
            c.applyDescription(this.agent);
        }
    }

    private void applyBinaryDescriptions(Variable v) {
        System.out.println("/////////////////////// apply Binary Descriptions: " + v.getName());
        for (Constraint c : v.getBinaryConstraints()) {
            System.out.println("/////////////////////// binary constraint: " + c.getName());
            c.applyDescription(this.agent);
        }
    }

    public String findUsingCharacteristics(Variable v) {
        if (v.getCubeAgent() != null) {
            if (v.getCubeAgent().equalsIgnoreCase(this.agent.getUri())) {
                // local search
                // TODO only valid instancs?
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
                         return me.getUUID();
                    }
                }
            } else {
                // TODO: remote
                System.out.println("\n\n finding characteristics from remote agent! \n\n");
                String agent = v.getCubeAgent();

                CMessage msg = new CMessage();
                msg.setTo(agent);
                msg.setObject("resolution");
                msg.setBody("findUsingCharacteristics");
                msg.setAttachement(v);
                try {
                    CMessage resultmsg = ((ResolverImpl)this.resolver).sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            this.agent.addExternalElement(resultmsg.getBody().toString(), resultmsg.getFrom());
                            return resultmsg.getBody().toString();
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String createInstance(Variable v) {
        if (v != null) {

            // apply binary constraints

            Properties props = new Properties();
            for (Property p : v.getProperties()) {
                props.put(p.getName(), p.getValue());
            }
                try {
                    ManagedElement me = agent.newManagedElement(v.getNamespace(), v.getName(), props);
                    System.out.println(me.getTextualDescription());
                    if (me != null) {
                        if (!v.values.contains(me.getUUID())) {
                            //agent.getRuntimeModel().add(me);
                            for (Reference r : v.getReferences()) {
                                if (me.getReference(r.getName()) == null) {
                                    Reference tmp = me.addReference(r.getName(), r.isOnlyOne());
                                    if (tmp != null) {
                                        for (String re : r.getReferencedElements()) {
                                            tmp.addReferencedElement(re);
                                        }
                                    }
                                } else {
                                    Reference tmp = me.getReference(r.getName());
                                    if (tmp != null) {
                                        for (String re : r.getReferencedElements()) {
                                            tmp.addReferencedElement(re);
                                        }
                                    }
                                }
                            }

                            return me.getUUID();
                        }
                    }

                } catch (InvalidNameException e) {
                    e.printStackTrace();
                    return null;
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return null;
    }

    private void info(String msg) {
        System.out.println("[RESOLVER] " + msg);
    }

    private boolean evaluateSubject(Variable var, Element e) {
        Stack<Constraint> stack = new Stack<Constraint>();
        if (!evaluateSubject(var, e, stack)) {
            // vider stack
            while (!stack.isEmpty()) {
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
                if (c.check(this.agent) == false)
                    return false;
                else
                    System.out.println("** checked ok");
            }
            for (Characteristic car : e.getBinaryCharacteristics()) {
                Constraint c = addConstraint(var, car);
                System.out.println("** adding characteristic constraint for subject var: " + c.getName());
                stack.push(c);
                c.init(this.agent); // initialize object variable.
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
            info("** adding unary characteristic constraint for subject var: " + c.getName());
        }
        for (Characteristic car : e.getBinaryCharacteristics()) {
            Constraint c = addConstraint(var, car);
            info("** adding binary characteristic constraint for subject var: " + c.getName());
            buildObject(c.getObjectVariable(), (Element)car.getObject());
        }
        /*
        for (Objective car : e.getUnaryObjectives()) {
            Constraint c = addConstraint(var, car);
            info("** adding unary characteristic(obj) constraint for subject var: " + c.getName());
        }
        for (Objective car : e.getBinaryObjectives()) {
            Constraint c = addConstraint(var, car);
            info("** adding binary characteristic(obj) constraint for subject var: " + c.getName());
            buildObject(c.getObjectVariable(), (Element)car.getObject());
        } */

    }

    private Constraint addConstraint(Variable var, Objective objective) {
        Variable objvar = null;
        if (objective.getObject() instanceof Element) {
            // Binary
            Element obj = (Element)objective.getObject();
            objvar = new Variable(this.agent, obj.getNamespace(), obj.getName());
        } else {
            // Unary
            objvar = new Variable(this.agent, objective.getObject());
        }
        Constraint c = new Constraint(var, objective.getNamespace(), objective.getName(), objvar, true);
        if (objective.getResolutionStrategy() != null) {
            if (objective.getResolutionStrategy().equalsIgnoreCase("f")) {
                c.setResolutionStrategy(Constraint.FIND);
            } else if (objective.getResolutionStrategy().equalsIgnoreCase("fc")) {
                c.setResolutionStrategy(Constraint.FIND_OR_CREATE);
            } else if (objective.getResolutionStrategy().equalsIgnoreCase("c")) {
                c.setResolutionStrategy(Constraint.CREATE);
            }
        }
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

    private void print() {
        if (root != null) {
            System.out.println(root.getName());
            for (Constraint c : root.getConstraints()) {
                printconstraint(c, "\t");
            }
        }
    }

    private void printconstraint(Constraint c, String indation) {
        if (c != null) {
            if (c.isUnaryConstraint()) {
                System.out.println(indation + "---" + c.getName() + "---> (" + c.getObjectVariable().getValue() + ")");
            } else {
                System.out.println(indation + "---" + c.getName() + "---> " + c.getObjectVariable().getName());
                for (Constraint cc : c.getObjectVariable().getConstraints()) {
                    printconstraint(cc, indation+"\t");
                }
            }
        }
    }

}
