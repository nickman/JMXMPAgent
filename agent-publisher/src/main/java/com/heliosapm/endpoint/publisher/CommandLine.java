/**
Licensed to the Apache Software Foundation (ASF) under one

or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.endpoint.publisher;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * <p>Title: CommandLine</p>
 * <p>Description: Command line processor for publisher agent</p> 
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.endpoint.publisher.CommandLine</code></p>
 */

public class CommandLine {
	/** The parsed out JMXSpecs */
	protected JMXMPSpec[] specs = {};
	/** The Zookeeper connection string */
	protected String zookeep = null;
	/** The agent command to execute */
	protected AgentCommand command = null;
	/** The PID of the JVM process to install into */
	protected long pid = -1;

	/**
	 * Creates a new CommandLine
	 */
	public CommandLine() {		
	}
	
	public static void main(String[] args) {
		log("Testing CmdLine");
		final String[] xargs = {
			"install",
			"--specs",
			"foo,bar,jvm,kafka:8192:0.0.0.0:DefaultDomain;jdatasources,tomcat:8193:0.0.0.0:jboss",
//			"--zk",
//			"localhost:2181,localhost:2182"
		};
		CommandLine cl = new CommandLine();
		CmdLineParser parser = new CmdLineParser(cl);
	   	try {
    		parser.parseArgument(xargs);
//	   		parser.parseArgument();
//	   		if(cl.specs.length==0) {
//	   			throw new CmdLineException(parser, new Exception("No JMXMP Specs Configured"));
//	   		}
    		log("Config:" + cl);
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }		
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}

	/**
	 * Sets the JMXMPSpecs
	 * @param specs the JMXMPSpecs
	 */
	@Option(name="--specs", required=false, usage="Sets one or more JMXMPSpecs in the form of <comma separated endpoints>[:<JMXMP listenng port>][:<JMXMP bind interface>][:<JMX Domain Name>]", handler=JMXMPSpecOptionHandler.class)
	protected void setJMXMPSpecs(final JMXMPSpec[] specs) {
		this.specs = specs;
	}
	
	/**
	 * Sets the zookeeper connect string
	 * @param connectString the zookeeper connect string
	 */
	@Option(name="--zk", usage="Sets the Zookeeper connect string in the form of <host1>:<port1>[,<hostn>:<portn>]")
	protected void setZKConnectString(final String connectString) {
		if(connectString==null || connectString.trim().isEmpty()) throw new IllegalArgumentException("The passed ZK connect string was nul or empty");
		this.zookeep = connectString.trim();
	}
	
	/**
	 * Sets the agent command
	 * @param command the command to set
	 */
	@Argument(index=0, required=true, metaVar="COMMAND", usage="The agent command")
	protected void setCommand(final AgentCommand command) {
		this.command = command;
	}
	
	/**
	 * Sets the pid of the JVM process to install into
	 * @param pid the pid to set
	 */
	@Option(name="--pid", usage="The PID of the target JVM process")
	protected void setPid(long pid) {
		this.pid = pid;
	}
	


	/**
	 * Returns the JMXMP specs
	 * @return the specs
	 */
	public JMXMPSpec[] getJMXMPSpecs() {
		return specs.clone();
	}

	/**
	 * Returns the zookeeper connection string
	 * @return the zookeeper connection string
	 */
	public String getZookeep() {
		return zookeep;
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder("Publisher Configuration:");
		b.append("\n\tAgent Command: ").append(command);
		b.append("\n\tTarget PID: ").append(pid);
		b.append("\n\tJMXMP and Endpoint Specs:");
		for(JMXMPSpec spec: specs) {
			b.append("\n\t\t").append(spec);
		}
		b.append("\n\tZookeeper Connect String:").append(zookeep);
		b.append("\n");
		return b.toString();
	}

	/**
	 * Returns the configured agent command
	 * @return the configured agent command
	 */
	public AgentCommand getCommand() {
		return command;
	}

	/**
	 * Returns the pid of the JVM process to install into
	 * @return the pid of the JVM process to install into
	 */
	public long getPid() {
		return pid;
	}

}
