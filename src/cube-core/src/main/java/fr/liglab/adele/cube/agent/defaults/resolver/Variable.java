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
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 5:15 PM
 *
 *
 */
public class Variable extends AbstractManagedElement {

    //private Object value = null;

    private String name;
    private String namespace;

    boolean primitive = false;

    private CubeAgent agent;

    public int findStep = 0;

    List<Constraint> constraints = new ArrayList<Constraint>();

    public Stack<String> values = new Stack<String>();

    /**
     * Primitive.
     *
     * @param agent
     * @param value
     */
    public Variable(CubeAgent agent, String value) {
        super(agent);
        this.primitive = true;
        this.agent = agent;
        this.values.push(value);
    }

    public Variable(CubeAgent agent, String namespace, String name) {
        super(agent);
        this.namespace = namespace;
        this.name = name;
        this.primitive = false;
        this.agent = agent;
    }

    /*
    public Variable(CubeAgent agent, String value, boolean primitive) {
        super(agent);
        if (value != null && value instanceof ManagedElement) {
            this.namespace = ((ManagedElement)value).getNamespace();
            this.name = ((ManagedElement)value).getName();
        }
        this.values.push(value.toString());
        this.primitive = primitive;
    } */

    public boolean isPrimitive() {
        return primitive;
    }



    public CubeAgent getAgent() {
        return this.agent;
    }

    /*
    public void setValue(ManagedElement value) {
        this.value = value;
    }   */

    public String getValue() {
        if (this.values.size() > 0)
            return this.values.peek();
        else
            return null;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    public List<Constraint> getConstraints() {
        return this.constraints;
    }

    public List<Constraint> getUnaryConstraints() {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Constraint c : getConstraints()) {
            if (c.isUnaryConstraint() == true)
                result.add(c);
        }
        return result;
    }

    public List<Constraint> getBinaryConstraints() {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Constraint c : getConstraints()) {
            if (c.isBinaryConstraint() == true)
                result.add(c);
        }
        return result;
    }

    public List<Constraint> getObjectiveConstraints() {
        List<Constraint> result = new ArrayList<Constraint>();
        for (Constraint c : getConstraints()) {
            if (c.isObjectiveConstraint()==true) {
                result.add(c);
            }
        }
        return result;
    }

    public void addConstraint(Constraint c) {
        this.constraints.add(c);
    }

    public void removeConstraint(Constraint c) {
        if (c != null) {
            if (c.getSubjectVariable() == this)
                this.constraints.remove(c);
        }
    }

    /*
    public void addAlreadyTestedValue(String value) {
        if (value != null)
            values.push(value);
    }

    public String getTestedValue() {
        return this.values.pop();
    }

    public List<String> getAlreadyTestedValues() {
        return values.subList(0, values.size());
    }

    public boolean isTestedValue(String uri) {
        return this.values.contains(uri);
    } */
}
