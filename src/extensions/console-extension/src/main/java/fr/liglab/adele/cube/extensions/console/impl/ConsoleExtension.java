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

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.extensions.AbstractExtension;
import fr.liglab.adele.cube.extensions.ExtensionBundle;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 3:56 PM
 */
public class ConsoleExtension extends AbstractExtension {


    public ConsoleExtension(CubeAgent agent, ExtensionBundle bundle, Properties properties) {
        super(agent, bundle, properties);
    }

    public void run() {
        System.out.println("---------------- Console Extension -----------------");
    }

    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConstraintResolver getConstraintResolver(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
