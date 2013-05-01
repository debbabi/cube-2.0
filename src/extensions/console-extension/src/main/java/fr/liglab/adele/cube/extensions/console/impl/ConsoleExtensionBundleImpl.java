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


package fr.liglab.adele.cube.extensions.console.impl;

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.cmf.ManagedElement;
import fr.liglab.adele.cube.agent.cmf.Property;
import fr.liglab.adele.cube.agent.cmf.Reference;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.ExtensionBundle;
import fr.liglab.adele.cube.extensions.console.ConsoleExtensionBundle;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:56 PM
 */
@Component(public_factory = true, immediate = true)
@Provides(specifications={ConsoleExtensionBundleImpl.class, ExtensionBundle.class})
@Instantiate
public class ConsoleExtensionBundleImpl  implements ConsoleExtensionBundle {


    List<ConsoleExtension> ams = new ArrayList<ConsoleExtension>();

    @Requires
    CubePlatform cps;

    @ServiceProperty(name = "osgi.command.scope", value = "cube")
    String m_scope;

    @ServiceProperty(name = "osgi.command.function", value = "{}")
    String[] m_function = new String[] { "version", "arch", "rm" /*, "newi", "extensions", "extension"*/};


    @Descriptor("Show Cube Platform Version")
    public void version() {
        System.out.println("\nCube Platform version: " + this.cps.getVersion() +"\n");

    }

    @Descriptor("Show archtype")
    public void arch() {
        for (String id : this.cps.getCubeAgents()) {
            CubeAgent ci = cps.getCubeAgent(id);
            if (ci != null) {
                System.out.println("--------------------------------------------------------------------------");
                if (ci.getArchetype() != null) {
                    System.out.println(ArchetypeParser.toXmlString(ci.getArchetype()));
                } else {
                    System.out.println("... archetype null!");
                }
                System.out.println("--------------------------------------------------------------------------");
            } else {
                System.out.println("... cube agent null!");
            }
        }
    }

    @Descriptor("Shows the internal model at runtime of the given Cube Instance")
    public void rm() {
        for (String id : this.cps.getCubeAgents()) {
            CubeAgent ci = cps.getCubeAgent(id);

            if (ci != null) {
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("------ UNMANAGED ----");
                for (ManagedElement e : ci.getRuntimeModel().getManagedElements(ManagedElement.UNMANAGED)) {
                    showManagedElementInformation(e);
                }
                System.out.println("------ UNCHECKED ----");
                for (ManagedElement e : ci.getRuntimeModel().getManagedElements(ManagedElement.UNCHECKED)) {
                    showManagedElementInformation(e);
                }
                System.out.println("------ VALID --------");
                for (ManagedElement e : ci.getRuntimeModel().getManagedElements(ManagedElement.VALID)) {
                    showManagedElementInformation(e);
                }
                System.out.println("--------------------------------------------------------------------------");
            }

        }
    }

    private void showManagedElementInformation(ManagedElement e) {
        System.out.println(" + " + e.getUUID());
        if (e.getProperties().size() > 0) {
            System.out.println("    | PROPERTIES");
            for (Property p : e.getProperties()) {
                System.out.println("    |   " + p.getName() + "=" + p.getValue());
            }
        }
        if (e.getReferences().size() > 0) {
            System.out.println("    | REFERENCES");
            for (Reference r : e.getReferences()) {
                System.out.println("    |   " + r.getName() + ":" );
                for (String s : r.getReferencedElements()) {
                    System.out.println("    |     " + s );
                }
            }
        }
        System.out.println("");
    }

    public String getName() {
        return NAME;
    }

    public String getPrefix() {
        return PREFIX;
    }

    public String getNamespace() {
        return NAMESPACE;
    }

    public Extension getExtensionInstance(CubeAgent agent, Properties properties) {
        return new ConsoleExtension(agent, this, properties);
    }
}
