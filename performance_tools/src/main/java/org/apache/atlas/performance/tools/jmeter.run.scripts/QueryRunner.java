/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.atlas.performance.tools.jmeter.run.scripts;

import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.jmeter.engine.StandardJMeterEngine;

import java.io.IOException;

public class QueryRunner {

    public static void run() throws IOException, InterruptedException {

        StandardJMeterEngine guidGenEngine;
        QueryTestBuilder queryTestBuilder = new QueryTestBuilder().withJmeterInitialized();

        System.out.println("Posting Tags");
        guidGenEngine = queryTestBuilder.
                withUserSessions(1,PropertiesFileReader.getNumTags()).
                withTestPlan("Post Tags").
                withResultGenerator().
                getEngine();
        guidGenEngine.run();

        System.out.println("num entries to tag"+PropertiesFileReader.getNumEntriesToTag());
        guidGenEngine = queryTestBuilder.
                withUserSessions(1,PropertiesFileReader.getNumEntriesToTag()).
                withTestPlan("Associate Tags").
                withResultGenerator().
                getEngine();
        guidGenEngine.run();

        Integer[] usersList = PropertiesFileReader.getNumUsers();
        Integer[] loopsList = PropertiesFileReader.getNumLoops();

        for (int i = 0; i < usersList.length; i++) {
            guidGenEngine = queryTestBuilder.
                    withUserSessions(usersList[i], loopsList[i]).
                    withTestPlan("Test User Queries").
                    withResultGenerator().
                    getEngine();
            guidGenEngine.run();
        }


       /* guidGenEngine = queryTestBuilder.
                withUserSessions(1,PropertiesFileReader.getNumTags()).
                withTestPlan("Get Entities Associated To Tags").
                withResultGenerator().
                getEngine();
        guidGenEngine.run();*/

    }
}