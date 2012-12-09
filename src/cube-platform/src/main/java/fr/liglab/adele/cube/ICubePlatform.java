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

package fr.liglab.adele.cube;

/**
 * Cube Platform OSGi Service Interface
 * 
 * @author debbabi
 *
 */
public interface ICubePlatform {
	
	public static final String CUBE_HOST = "cube-host";
	public static final String CUBE_PORT = "cube-port";

	public static final String DEFAULT_CUBE_HOST = "localhost";
	public static final String DEFAULT_CUBE_PORT = "38400";
	
	/**
	 * Get Cube Platform Version	
	 * @return Version number
	 */
	public String getVersion();
	
	/**
	 * Get Cube Platform Host
	 * @return host name (or IP adress)
	 */
	public String getHost();
	
	/**
	 * Get Cube Platform network port
	 * @return port
	 */
	public long getPort();
	
}	
