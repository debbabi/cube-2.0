/*
 * Copyright 2011-2012 Adele Research Group (http://adele.imag.fr/) 
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

/**
 * Cube Platform Implementation.
 * 
 * It manages the local {@link fr.liglab.adele.cube.agent.CubeAgent Cube Agents}.
 * 
 * @author debbabi
 *
 */
package fr.liglab.adele.cube.platform;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;

import fr.liglab.adele.cube.ICubeAgent;
import fr.liglab.adele.cube.ICubePlatform;

public class CubePlatform implements ICubePlatform {

	public static final String CUBE_VERSION = "2.0";
	
	/**
	 * OSGi Bundle Context
	 */
	private BundleContext bundleContext;
	
	/**
	 * <CubeAgentID, ICubeAgent>
	 */
	Map<String, ICubeAgent> cubeAgents = new HashMap<String, ICubeAgent>();

	/**
	 * Constructor
	 * @param btx OSGi Bundle Context. It is injected automatically by iPOJO
	 */
	public CubePlatform(BundleContext btx) {
		this.bundleContext = btx;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {		
		return CUBE_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHost() {
		String host = this.bundleContext.getProperty(CUBE_HOST);
		if (host == null) {
			return DEFAULT_CUBE_HOST;
		}
		return host;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getPort() {
		String port = this.bundleContext.getProperty(CUBE_PORT);
		if (port == null) {
			try {
			return new Long(DEFAULT_CUBE_PORT).longValue();
			} catch(NumberFormatException ex) {
				System.out.println("The Cube specified port is not valid! We will use the default value " + DEFAULT_CUBE_PORT);
				port = DEFAULT_CUBE_PORT;
			}
		}
		return new Long(port).longValue();
	}
	
	/**
	 * Called when the Cube Platform starts on the local OSGi Platform.
	 */
	public void starting() {
		System.out.println(" ");
		System.out.println("");
		System.out.println("    _______              ");
		System.out.println("   /|      |             ");
		System.out.println("  | | CUBE |...Starting CUBE Platform  ");
 	    System.out.println("  | |______|   version: " + getVersion());	
		System.out.println("  |/______/              ");
		System.out.println("");
	}
	
	/**
	 * Called when the Cube Platform stops on the local OSGi Platform.
	 * All the created Cube Agents will be destroyed.
	 */
	public void stopping() {
		System.out.println(" ");
		System.out.println("[INFO] ... Stopping the CUBE Platform");
		for (ICubeAgent ci: this.cubeAgents.values()) {
			ci.stop();
		}
		System.out.println("[INFO] ... Bye!");
		System.out.println(" ");

	}

}
