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

import fr.liglab.adele.cube.agent.*;
import fr.liglab.adele.cube.agent.defaults.resolver.Constraint;
import fr.liglab.adele.cube.cmf.ManagedElement;
import fr.liglab.adele.cube.cmf.Notification;
import fr.liglab.adele.cube.agent.defaults.resolver.ResolutionGraph;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;

import java.io.IOException;

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
        switch (notification.getNotificationType()) {
            case RuntimeModelListener.NEW_UNCHECKED_INSTANCE: {
                Object instance = notification.getNewValue();
                if (instance != null && instance instanceof ManagedElement) {
                    resolveNewInstance((ManagedElement)instance);
                }
            } break;
        }
    }

    private void resolveNewInstance(ManagedElement instance) {
        System.out.println("[RESOLVER] resolve.new.unmanaged.instance: " + instance.getUUID());
        /*
         * Create the root variable that contains the newly created instance (to be resolved).
         */
        Variable var = new Variable(agent, instance.getNamespace(), instance.getName());
        var.setValue(instance.getUUID());

        /*
         * Create a Resolution Graph (Constraints Graph).
         */
        ResolutionGraph constraintsGraph = new ResolutionGraph(this);
        /*
         * Set the root variable.
         */
        constraintsGraph.setRoot(var);

        /*
         * Start the resolution processs.
         */
        if (constraintsGraph.resolve()) {

            validateSolution(constraintsGraph);

        } else {

        }
    }

    /**
     * Validate the found solution.
     * @param graph
     */
    void validateSolution(ResolutionGraph graph) {
         System.out.println("\nvalidating solution..\n");

        if (graph != null) {
            if (graph.getRoot() != null) {
                validateVariable(graph.getRoot());
            }
        }
        //((RuntimeModelControllerImpl) ((RuntimeModelImpl) this.agent.getRuntimeModel()).getController()).validate(instance);
        // TODO:
    }

    void validateVariable(Variable v) {
        if (v.getValue() != null) {
            //
            if (!v.isPrimitive()) {
                for (Constraint c : v.getBinaryConstraints()) {
                    validateVariable(c.getObjectVariable());
                }
            }
            //
            Object uuid =v.getValue();
            if (uuid != null) {
                ManagedElement me = agent.getRuntimeModelController().getLocalElement(uuid.toString());
                if (me != null) {
                    if (me.getState() == ManagedElement.UNCHECKED) {
                        ((AbstractManagedElement)me).validate();
                    } else if (me.getState() == ManagedElement.UNMANAGED) {
                        agent.getRuntimeModel().add(me);
                    }
                }
            }
        }
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
     * @param csp
     */
    void backtrackingSearch(ResolutionGraph csp) {
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

    public CubeAgent getCubeAgent() {
        return this.agent;
    }

    public void receiveMessage(CMessage msg) {
        if (msg.getCorrelation() == waitingCorrelation) {
            this.waitingMessage = msg;
            if (csplock != null) {
                synchronized (csplock) {
                    csplock.notify();
                }
            }
            waitingCorrelation = -1;
        }
        try {
            handleMessage(msg);
        }  catch(Exception ex) {
            //getCubeAgent().getLogger().error("[AbstractExtension.receiveMessage] " + ex.getMessage());
        }
    }

    protected void handleMessage(CMessage msg) throws Exception {

        if (msg != null) {
            System.out.println("\n\n received message to resolve! \n\n "+msg.toString()+" \n\n");
            if (msg.getBody() != null && msg.getBody().toString().equalsIgnoreCase("findUsingCharacteristics")) {
                if (msg.getAttachement() != null && msg.getAttachement() instanceof Variable) {
                    ResolutionGraph constraintsGraph = new ResolutionGraph(this);
                    Variable v = (Variable)msg.getAttachement();
                    String result = constraintsGraph.findUsingCharacteristics(v);

                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(result);
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void send(CMessage msg) throws Exception {
        if (msg != null) {
            msg.setFrom(getCubeAgent().getUri());
            msg.setReplyTo(getCubeAgent().getUri());
            /*if (msg.getTo() != null && !msg.getTo().contains("/ext/"))
                msg.setTo(msg.getTo() + "/ext/" + getExtensionFactory().getExtensionId());*/
            getCubeAgent().getCommunicator().sendMessage(msg);
        }
    }

    public CMessage sendAndWait(CMessage msg) throws TimeOutException {
        if (msg != null) {
            msg.setFrom(getCubeAgent().getUri());
            msg.setReplyTo(getCubeAgent().getUri());
            /*if (msg.getTo() != null && !msg.getTo().contains("/ext/"))
                msg.setTo(msg.getTo() + "/ext/" + getExtensionFactory().getExtensionId());*/
            //String to = msg.getTo();
            msg.setCorrelation(++correlation);
            waitingCorrelation = msg.getCorrelation();
            //System.out.println(msg.toString());
            try {
                this.waitingMessage = null;

                this.getCubeAgent().getCommunicator().sendMessage(msg);
            } catch (Exception e) {
                //this.getCubeAgent().getLogger().warning("The Extension could not send a message to " + to + "!");
            }
            try {
                long initialTime = System.currentTimeMillis();
                long currentTime = initialTime;
                long waitingTime = TIMEOUT;
                synchronized (csplock) {
                    while (((currentTime < (initialTime + TIMEOUT)) && waitingTime > 1)
                            && (this.waitingMessage == null)) {
                        csplock.wait(waitingTime);
                        currentTime = System.currentTimeMillis();
                        waitingTime = waitingTime - (currentTime - initialTime);
                    }
                }
            } catch (InterruptedException e) {
                //this.getCubeAgent().getLogger().warning("The Extension waits for a response message from " + to + " but no answer! timeout excedded!");
            }
            return this.waitingMessage;
        } else {
            return null;
        }
    }

    private CMessage waitingMessage = null;
    private long TIMEOUT = 3000;
    private Object csplock = new Object();
    private static long correlation = 1;
    private long waitingCorrelation = -1;

}
