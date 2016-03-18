package org.apache.atlas.performance.tools;

import org.apache.jmeter.engine.StandardJMeterEngine;

import java.io.IOException;

public class QueryRunner {
    public static void main(String[] args) throws IOException {

        QueryRunner queryRunner = new QueryRunner();
        queryRunner.run();
    }

    private void run() throws IOException {
        StandardJMeterEngine engine = QueryTestBuilder.newInstance().
                withUserSessions(5).
                withTestPlan().
                withResultGenerator().
                getEngine();
        engine.run();
    }
}
