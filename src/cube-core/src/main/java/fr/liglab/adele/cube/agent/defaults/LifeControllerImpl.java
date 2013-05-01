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

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 1:32 AM
 */
public class LifeControllerImpl implements LifeController {

    private String uri;

    private CubeAgent agent;

    public LifeControllerImpl(CubeAgent agent) {
        this.agent = agent;
        this.uri = agent.getUri() + "/life";
        try {
            this.agent.getCommunicator().addMessagesListener(this.uri, new MessagesListener() {
                public void receiveMessage(CMessage msg) {
                      //System.out.println("LIFE: receive...\n"+msg.toString());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void run() {


    }

    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
