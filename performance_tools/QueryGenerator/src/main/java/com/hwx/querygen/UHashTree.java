package com.hwx.querygen;

import org.apache.jorphan.collections.HashTree;

/**
 * Created by temp on 3/2/16.
 */
public class UHashTree {
    HashTree threadGroupHashTree;
    UHashTree() {
       threadGroupHashTree = new HashTree();
    }
public void setHashTree(HashTree MainTestPlanTree,String testPlan,String threadGroupName) {
    threadGroupHashTree = MainTestPlanTree.add(new UTestPlan(testPlan).getTestPlan(),new UThreadGroup().generateUThreadGroup(threadGroupName));
    UHTTPSampler sampler=new UHTTPSampler();
    threadGroupHashTree.add(sampler.getUHTTPSamplerQ1());
    threadGroupHashTree.add(sampler.getUHTTPSamplerQ2());
    threadGroupHashTree.add(sampler.getUHTTPSamplerQ3());
    //return threadGroupHashTree;
}
}
