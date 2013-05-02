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


package fr.liglab.adele.cube.tools.console;

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.cmf.*;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Descriptor;

import java.util.Properties;


/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:56 PM
 */
@Component(public_factory = true, immediate = true)
@Provides(specifications = {CubeConsole.class})
@Instantiate
public class CubeConsole {

    @Requires
    CubePlatform cps;

    @ServiceProperty(name = "osgi.command.scope", value = "cube")
    String m_scope;

    @ServiceProperty(name = "osgi.command.function", value = "{}")
    String[] m_function = new String[]{"version", "agents", "arch", "rm" , "newi" /*, "extensions", "extension"*/};


    @Descriptor("Show Cube Platform Version")
    public void version() {
        String msg = "--------------------------------------------------------------------------";
        msg += "\nCube Platform version: " + this.cps.getVersion();
        msg += "\n--------------------------------------------------------------------------";
        System.out.println(msg);

    }

    @Descriptor("Show created Cube Agents")
    public void agents() {
        String msg = "--------------------------------------------------------------------------";
        for (String uri : this.cps.getCubeAgents()) {
            CubeAgent ci = cps.getCubeAgent(uri);
            msg += "\n[" + ci.getLocalId() + "] " + ci.getUri();

        }
        msg += "\n--------------------------------------------------------------------------";
        System.out.println(msg);

    }

    @Descriptor("Show archtype")
    public void arch(@Descriptor("Agent local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Agent you want to see its archetype.\nType 'cube:agents' to see the list of existing Cube Agents in this platform.");
            return;
        }
        CubeAgent agent = cps.getCubeAgentByLocalId(aid);
        if (agent == null) {
            System.out.println("Agent '"+aid+"' does not exist! Type 'cube:agents' to see the list of existing Cube Agents in this platform.");
        } else {
            String msg = "--------------------------------------------------------------------------";
            if (agent.getArchetype() != null) {
                msg += "\n" + ArchetypeParser.toXmlString(agent.getArchetype());
            } else {
                msg += "\n... No archetype!";
            }
            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Shows the internal model at runtime of the given Cube Instance")
    public void rm(@Descriptor("Agent local id") String aid) {
        if (aid == null) {
            System.out.println("You should provide which Cube Agent you want to see its Runtime Model.\nType 'cube:agents' to see the list of existing Cube Agents in this platform.");
            return;
        }
        CubeAgent agent = cps.getCubeAgentByLocalId(aid);
        if (agent == null) {
            System.out.println("Agent '"+aid+"' does not exist! Type 'cube:agents' to see the list of existing Cube Agents in this platform.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            msg += "\n------ UNMANAGED ----";
            for (ManagedElement e : agent.getRuntimeModel().getManagedElements(ManagedElement.UNMANAGED)) {
                msg += showManagedElementInformation(e);
            }
            msg += "\n" + "------ UNCHECKED ----";
            for (ManagedElement e : agent.getRuntimeModel().getManagedElements(ManagedElement.UNCHECKED)) {
                msg += showManagedElementInformation(e);
            }
            msg += "\n" + "------ VALID --------";
            for (ManagedElement e : agent.getRuntimeModel().getManagedElements(ManagedElement.VALID)) {
                msg += showManagedElementInformation(e);
            }

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    private String showManagedElementInformation(ManagedElement e) {
        String msg = "";
        msg += "\n + " + e.getUri();
        if (e.getProperties().size() > 0) {
            msg += "\n    | PROPERTIES";
            for (Property p : e.getProperties()) {
                msg += "\n    |   " + p.getName() + "=" + p.getValue();
            }
        }
        if (e.getReferences().size() > 0) {
            msg += "\n    | REFERENCES";
            for (Reference r : e.getReferences()) {
                msg += "\n    |   " + r.getName() + ":";
                for (String s : r.getReferencedElements()) {
                    msg += "\n    |     " + s;
                }
            }
        }
        return msg;
    }

    @Descriptor("Show archtype")
    public void newi(@Descriptor("Agent local id") String aid,
                     @Descriptor("Element type") String type) {
        if (aid == null) {
            System.out.println("You should provide which Cube Agent you want to see its archetype.\nType 'cube:agents' to see the list of existing Cube Agents in this platform.");
            return;
        }
        CubeAgent agent = cps.getCubeAgentByLocalId(aid);
        if (agent == null) {
            System.out.println("Agent '"+aid+"' does not exist! Type 'cube:agents' to see the list of existing Cube Agents in this platform.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            if (type != null) {
                String typens = CoreExtensionFactory.NAMESPACE;
                String typename = type;
                if (type.contains(":")) {
                    String[] tmp = type.split(":");
                    if (tmp != null && tmp.length == 2) {
                        typens = tmp[0];
                        typename = tmp[1];
                    }
                }
                Extension e = agent.getExtension(typens);
                if (e != null) {
                    ManagedElement me = e.newManagedElement(typename);
                    if (me != null) {
                        agent.getRuntimeModel().add(me);
                        msg += "\n... instance created: " + me.getUri();
                    }
                }

            } else {
                msg += "\n... error!";
            }

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }

    @Descriptor("Show archtype")
    public void newi(@Descriptor("Agent local id") String aid,
                     @Descriptor("Element type") String type,
                     @Descriptor("Element properties") String properties) {
        if (aid == null) {
            System.out.println("You should provide which Cube Agent you want to see its archetype.\nType 'cube:agents' to see the list of existing Cube Agents in this platform.");
            return;
        }
        CubeAgent agent = cps.getCubeAgentByLocalId(aid);
        if (agent == null) {
            System.out.println("Agent '"+aid+"' does not exist! Type 'cube:agents' to see the list of existing Cube Agents in this platform.");
        } else {
            String msg = "--------------------------------------------------------------------------";

            if (type != null) {
                String typens = CoreExtensionFactory.NAMESPACE;
                String typename = type;
                if (type.contains(":")) {
                    String[] tmp = type.split(":");
                    if (tmp != null && tmp.length == 2) {
                        typens = tmp[0];
                        typename = tmp[1];
                    }
                }
                Extension e = agent.getExtension(typens);
                if (e != null) {
                    Properties p = new Properties();
                    if (properties != null && properties.length() > 0) {
                        String[] tmp = properties.split(",");
                        if (tmp != null && tmp.length > 0) {
                            for (int i =0; i<tmp.length; i++) {
                                String[] prop = tmp[i].split(":");
                                if (prop != null && prop.length == 2) {
                                    p.put(prop[0], prop[1]);
                                }
                            }
                        }

                        ManagedElement me = null;
                        try {
                            me = e.newManagedElement(typename, p);
                        } catch (InvalidNameException e1) {
                            e1.printStackTrace();
                        } catch (PropertyExistException e1) {
                            e1.printStackTrace();
                        }

                        if (me != null) {
                            agent.getRuntimeModel().add(me);
                            msg += "\n... instance created: " + me.getUri();
                        }
                    } else {
                        ManagedElement me = e.newManagedElement(typename);
                        if (me != null) {
                            agent.getRuntimeModel().add(me);
                            msg += "\n... instance created: " + me.getUri();
                        }
                    }

                }

            } else {
                msg += "\n... error!";
            }

            msg += "\n--------------------------------------------------------------------------";
            System.out.println(msg);
        }
    }
}
