package com.hwx.querygen;

/**
 * Created by temp on 3/3/16.
 */
public class AttributeGenerator implements IQuery
{
    @Override
    public String generateQuery() {
        return new Table_Given_Name().generateQuery()+"+select+owner,name,createTime,comment,tableType,temporary,lastAccessTime,retention,viewExpandedText,viewOriginalText";
    }
    //+select+owner,name,createTime,comment,tableType,temporary,lastAccessTime,retention,viewExpandedText,viewOriginalText
  //  new Table_Given_Name()


}
