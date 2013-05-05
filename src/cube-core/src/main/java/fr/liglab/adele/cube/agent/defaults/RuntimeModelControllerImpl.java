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
import fr.liglab.adele.cube.agent.defaults.resolver.ResolutionGraph;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;
import fr.liglab.adele.cube.cmf.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:49 PM
 */
public class RuntimeModelControllerImpl implements RuntimeModelController {

    CubeAgent agent;

    public RuntimeModelControllerImpl(CubeAgent agent) {
        this.agent = agent;
    }

    /*
    protected String validate(ManagedElement element) {
        if (element != null) {
            ((AbstractManagedElement)element).updateState(ManagedElement.VALID);
        }

        //if (isInRuntimeModel(this.runtimeModel, element)) {

        //}
        //((RuntimeModelImpl)this.runtimeModel).addUnmanagedElement(element);
        return element.getUUID();
    }*/

    /**
     * Add an element to the local Runtime Model.
     * It puts its initial state to UNMANAGED.
     * @param element
     * @return
     */
    public String addManagedElement(ManagedElement element) {
        if (element != null) {
            ((AbstractManagedElement)element).updateState(ManagedElement.UNMANAGED);
        }
        //((RuntimeModelImpl)this.runtimeModel).addUnmanagedElement(element);
        return element.getUUID();
    }

    public String getAgentOfElement(String managed_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.getCubeAgent();
        } else {
            String s = this.agent.getExternalAgentUri(managed_element_uuid);
            return s;
        }
    }

    public boolean setAgentOfElement(String managed_element_uuid, String agentUri) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            ((AbstractManagedElement)me1).setCubeAgent(agentUri);
            return true;
        }
        return false;
    }

    /*
    public List<Property> getProperties(String managed_element_uuid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    } */

    public String getPropertyValue(String managed_element_uuid, String name) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.getProperty(name);
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getPropertyValue");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", name);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
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

    public boolean addProperty(String managed_element_uuid, String name, String value) throws PropertyExistException, InvalidNameException {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.addProperty(name, value);
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("addProperty");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", name);
                msg.addHeader("value", value);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String updateProperty(String managed_element_uuid, String name, String newValue) throws PropertyNotExistException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getReferencedElements(String managed_element_uuid, String reference_name) {
        System.out.println("CONTROLLER: get referenced elements for: " + managed_element_uuid + " >" + reference_name);
        List<String> result = new ArrayList<String>();
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            System.out.println("CONTROLLER: element>\n" + me1.getTextualDescription());
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.getReferencedElements();
            } else {
                System.out.println("CONTROLLER: no reference with name: " + reference_name);
            }
        } else {
            System.out.println("CONTROLLER: no element with uuid: " + managed_element_uuid);
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("getReferencedElements");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            String[] tmp = resultmsg.getBody().toString().split(",");
                            for (int i=0; i<tmp.length; i++) {
                                if (tmp[i] != null && tmp[i].length()>0)
                                    result.add(tmp[i]);
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;



    }

    public boolean addReference(String managed_element_uuid, String reference_name, String referenced_element_uuid) throws InvalidNameException {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.addReference(reference_name, false);
            if (r != null) {
                r.addReferencedElement(referenced_element_uuid);
                return true;
            }
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("addReference");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                msg.addHeader("refuuid", referenced_element_uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean removeReference(String managed_element_uuid, String reference_name, String referenced_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.removeReferencedElement(referenced_element_uuid);
            }
        } else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("removeReference");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                msg.addHeader("refuuid", referenced_element_uuid);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean hasReferencedElements(String managed_element_uuid, String reference_name, String referenced_element_uuri) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.hasReferencedElement(referenced_element_uuri);
            }
        }  else {
            String auri = agent.getExternalAgentUri(managed_element_uuid);
            if (auri != null) {
                CMessage msg = new CMessage();
                msg.setTo(auri);
                msg.setObject("runtimemodel");
                msg.setBody("hasReferencedElements");
                msg.addHeader("uuid", managed_element_uuid);
                msg.addHeader("name", reference_name);
                msg.addHeader("refuuid", referenced_element_uuri);
                try {
                    CMessage resultmsg = sendAndWait(msg);
                    if (resultmsg != null) {
                        if (resultmsg.getBody() != null) {
                            if (resultmsg.getBody().toString().equalsIgnoreCase("true")) {
                                return true;
                            }
                        }
                    }
                } catch (TimeOutException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
                    /*
    public List<String> getElements(String agentUri, String namespace, String name) {
        List<String> result = new ArrayList<String>();
        if (this.agent.getUri().equalsIgnoreCase(agentUri)) {
            for (ManagedElement me : this.agent.getRuntimeModel().getManagedElements()) {
                result.add(me.getUUID());
            }
        } else {
            // remote find

        }
        return result;
    }                 */

    public ManagedElement getLocalElement(String managed_element_uuid) {
        ManagedElement me = agent.getRuntimeModel().getManagedElement(managed_element_uuid);
        if (me != null)
            return me;
        else {
            for (ManagedElement unme : agent.getUnmanagedElements()) {
                if (unme.getUUID().equalsIgnoreCase(managed_element_uuid)) {
                    return unme;
                }
            }
        }
        System.out.println("[WARNING] RM.Constroller has not find the element " + managed_element_uuid);
        return null;
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
            if (msg.getBody() != null) {
                if (msg.getBody().toString().equalsIgnoreCase("getPropertyValue")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");

                    String p = null;
                    if (uuid != null && name != null) {
                        p = getPropertyValue(uuid.toString(), name.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(p);
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } if (msg.getBody().toString().equalsIgnoreCase("addProperty")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object value = msg.getHeader("value");

                    boolean p = false;
                    if (uuid != null && name != null && value != null) {
                        p = addProperty(uuid.toString(), name.toString(), value.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } if (msg.getBody().toString().equalsIgnoreCase("getReferencedElements")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");

                    String resultat = "";
                    if (uuid != null && name != null) {
                        List<String> res = getReferencedElements(uuid.toString(), name.toString());
                        for (String r : res) {
                            resultat += r + ",";
                        }
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    resmsg.setBody(resultat);

                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }   if (msg.getBody().toString().equalsIgnoreCase("addReference")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object refuuid = msg.getHeader("refuuid");

                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        p = addReference(uuid.toString(), name.toString(), refuuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }    if (msg.getBody().toString().equalsIgnoreCase("removeReference")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object refuuid = msg.getHeader("refuuid");

                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        p = removeReference(uuid.toString(), name.toString(), refuuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
                    try {
                        getCubeAgent().getCommunicator().sendMessage(resmsg);
                    } catch (CommunicationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }    if (msg.getBody().toString().equalsIgnoreCase("hasReferencedElements")) {
                    Object uuid = msg.getHeader("uuid");
                    Object name = msg.getHeader("name");
                    Object refuuid = msg.getHeader("refuuid");

                    boolean p = false;
                    if (uuid != null && name != null && refuuid != null) {
                        p = hasReferencedElements(uuid.toString(), name.toString(), refuuid.toString());
                    }
                    CMessage resmsg = new CMessage();
                    resmsg.setTo(msg.getFrom());
                    resmsg.setCorrelation(msg.getCorrelation());
                    resmsg.setObject(msg.getObject());
                    if (p == true)
                        resmsg.setBody("true");
                    else
                        resmsg.setBody("false");
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

    CubeAgent getCubeAgent() {
        return this.agent;
    }

    private CMessage waitingMessage = null;
    private long TIMEOUT = 3000;
    private Object csplock = new Object();
    private static long correlation = 1;
    private long waitingCorrelation = -1;
}
