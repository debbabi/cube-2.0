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

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.Extension;

/**
 *
 *
 * Unary Constraint: restricts the value of a single variable.
 * Binary Constraint: relates two variables.
 *
 * Higher-order constraints that involve three of more variables
 * can be represented by a collection of binary constraints.
 *
 *
 * Author: debbabi
 * Date: 4/28/13
 * Time: 5:37 PM
 *
 */
public class Constraint {

    /**
     * Constraint Type.
     */
    private String namespace;
    private String name;

    private Variable subjectVariable;
    private Variable objectVariable;

    private boolean objectiveConstraint = false;


    public Constraint(Variable subjectVar, String namespace, String name, Variable objectVar) {
        this(subjectVar, namespace, name, objectVar, false);
    }

    public Constraint(Variable subjectVar, String namespace, String name, Variable objectVar, boolean objectiveConstraint) {
        this.subjectVariable = subjectVar;
        this.namespace = namespace;
        this.name = name;
        this.objectVariable = objectVar;
        this.objectiveConstraint = objectiveConstraint;

        this.subjectVariable.addConstraint(this);
    }


    public Variable getObjectVariable() {
        return objectVariable;
    }

    public Variable getSubjectVariable() {
        return subjectVariable;
    }

    public boolean isUnaryConstraint() {
        return this.objectVariable != null && this.objectVariable.isPrimitive();
    }

    public boolean isBinaryConstraint() {
        return this.objectVariable != null && !this.objectVariable.isPrimitive();
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public boolean isObjectiveConstraint() {
        return objectiveConstraint;
    }

    /**
     * Initialize the object variable value from the subject variable value.
     * Call the real constraint identified by namespace:name.
     */
    public void init(CubeAgent agent) {
        if (objectVariable.isPrimitive() == false) {
            Extension e = this.subjectVariable.getAgent().getExtension(namespace);
            if (e != null) {
                ConstraintResolver cr = e.getConstraintResolver(name);
                if (cr != null) {
                    cr.init(agent, subjectVariable, objectVariable);
                }
            }
        }
    }

    /**
     * Check if the constraint is true.
     * Call the real constraint identified by namespace:name.
     * @return
     */
    public boolean check(CubeAgent agent) {
        if (subjectVariable.values.size() > 0 && objectVariable.values.size() > 0) {
            Extension e = this.subjectVariable.getAgent().getExtension(namespace);
            if (e != null) {
                ConstraintResolver cr = e.getConstraintResolver(name);
                if (cr != null) {
                    return cr.check(agent, subjectVariable, objectVariable);
                }
            }
        }
        return false;
    }

    public void applyCharacteristic(CubeAgent agent) {
        if (objectVariable.hasValue()) {
            Extension e = this.subjectVariable.getAgent().getExtension(namespace);
            if (e != null) {
                ConstraintResolver cr = e.getConstraintResolver(name);
                if (cr != null) {
                    cr.applyCharacteristic(agent, subjectVariable, objectVariable);
                }
            }
        }
    }

    /**
     * If subject or object are remote instances?
     */
    public void applyObjective(CubeAgent agent) {
        if (subjectVariable.hasValue() && objectVariable.hasValue()) {
            Extension e = this.subjectVariable.getAgent().getExtension(namespace);
            if (e != null) {
                ConstraintResolver cr = e.getConstraintResolver(name);
                if (cr != null) {
                    cr.applyObjective(agent, subjectVariable, objectVariable);
                }
            }
        }
    }

    public void cancelObjective(CubeAgent agent) {
        if (subjectVariable.values.size() > 0 && objectVariable.values.size() > 0) {
            Extension e = this.subjectVariable.getAgent().getExtension(namespace);
            if (e != null) {
                ConstraintResolver cr = e.getConstraintResolver(name);
                if (cr != null) {
                    cr.cancelObjective(agent, subjectVariable, objectVariable);
                }
            }
        }
    }

    /**
     * Find subject variable when knowing the object var.
     */
    public String find(CubeAgent agent) {
        if (subjectVariable.values.size() > 0 && objectVariable.values.size() > 0) {
            Extension e = this.subjectVariable.getAgent().getExtension(namespace);
            if (e != null) {
                ConstraintResolver cr = e.getConstraintResolver(name);
                if (cr != null) {
                    return cr.find(agent, subjectVariable, objectVariable);
                }
            }
        }
        return null;
    }
}
