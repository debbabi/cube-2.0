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


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.Resolver;
import fr.liglab.adele.cube.agent.RuntimeModelListener;
import fr.liglab.adele.cube.agent.cmf.ManagedElement;
import fr.liglab.adele.cube.agent.cmf.Notification;
import fr.liglab.adele.cube.agent.defaults.resolver.Assignment;
import fr.liglab.adele.cube.agent.defaults.resolver.ResolutionGraph;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:18 PM
 */
public class ResolverImpl implements Resolver, RuntimeModelListener {

    private CubeAgent agent;

    public ResolverImpl(CubeAgent agent) {
        this.agent = agent;
        if (agent == null)
            throw new NullPointerException();
        if (agent != null) {
            agent.getRuntimeModel().addListener(this);

        }
    }

    public void update(RuntimeModelImpl rm, Notification notification) {
        System.out.println("[Resolver] notified! " + notification.getNotificationType());
        switch (notification.getNotificationType()) {
            case RuntimeModelListener.NEW_UNMANAGED_INSTANCE: {
                Object instance = notification.getNewValue();
                if (instance != null && instance instanceof ManagedElement) {
                    resolveNewInstance((ManagedElement)instance);
                }
            } break;
        }
    }

    private void resolveNewInstance(ManagedElement instance) {
        System.out.println("[Resolver] resolve.new.unmanaged.instance: " + instance.getUUID());

        /**
         * The set of values
         */
        Assignment assignment;



        Variable var = new Variable(agent, instance.getNamespace(), instance.getName());
        //var.setValue(instance);
        var.values.push(instance.getUUID());
        ResolutionGraph constraintsGraph = new ResolutionGraph(this.agent);
        constraintsGraph.setRoot(var);

        if (constraintsGraph.resolve() == true) {
            validateSolution(constraintsGraph);
        }

        ((RuntimeModelControllerImpl) ((RuntimeModelImpl) this.agent.getRuntimeModel()).getController()).validate(instance);
    }

    void validateSolution(ResolutionGraph graph) {
        // TODO:
    }

    /**
     * FIND
     * @param v
     * @return
     */
    String find (Variable v) {

        return null;

    }

    /**
     * Recursive backtracking search algo.
     * Returns a solution, or failure.
     *
     * uses domain-specific heuristic functions derived from the knowledge of the problem.
     *
     * propagating information through constraints:
     *   Whenever a variable X is assigned, the forxard checking process looks at each unassigned
     *   variable Y that is connected to X by a constraint and deletes from Y's domain any value
     *   that is in consistent with the value chosen for X.
     *
     * Chronological backtracking:
     *   When a branch of the search fails! back up to the preceding variable and try a different value for it.
     *
     *
     *
     *
     * @param assignment
     * @param csp
     */
    void backtrackingSearch(Assignment assignment, ResolutionGraph csp) {
        /**
         * if assignement is complete then return assignement
         * var = select_unassigned_variable(variables[constraintsGraph], assignement, csp) do
         * for each 'value' in Order_Domain_Values(var, assignement, csp) do
         *   if 'value' is consistent with assignment according to Constraints[csp] then
         *      add {var = value} to assignement
         *      result = backtrackingSearch(assignement, csp)
         *      if (result != failure then return result;
         *      remove {var = value} from assignement
         * return failure
         */
    }

    void BT() {
        /**
         * Foreach Val in D[i]
         *      Assignments[i] = val.
         *      Consistent = true
         *      for h=1 To i-1 While Consistent
         *             Consistent = Test(i, h)
         *      if Consistent
         *          if i = n
         *                 Show ( Solution() )
         *          Else
         *              BT(i+1)
         *      return false;
         */
    }

}
