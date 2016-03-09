package com.hwx.querygen;

/**
 * Created by temp on 3/2/16.
 */
interface  IQuery {
    final static String DSL_TEMPLATE="/api/atlas/discovery/search/dsl?query=";
    String generateQuery();
}
