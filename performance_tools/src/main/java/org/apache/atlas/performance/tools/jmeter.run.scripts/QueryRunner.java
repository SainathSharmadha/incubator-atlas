package org.apache.atlas.performance.tools.jmeter.run.scripts;
import org.apache.atlas.performance.tools.PropertiesFileReader;
import org.apache.jmeter.engine.StandardJMeterEngine;

import java.io.IOException;

public class QueryRunner
{
    public static void main( String[] args ) throws InterruptedException {
       int nUsers= PropertiesFileReader.getNumUsers();
        int nLoops=PropertiesFileReader.getNumLoops();
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.run(nUsers,nLoops);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run(Integer nUsers,Integer nLoops) throws IOException, InterruptedException {
        StandardJMeterEngine guidGenEngine = new QueryTestBuilder().
                withJmeterInitialized().
                withUserSessions(nLoops,nUsers).
                withTestPlan().
                withResultGenerator().
                getEngine();
        guidGenEngine.run();

    }
}