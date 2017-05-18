package com.ibm;
import com.fasterxml.jackson.annotation.JsonView;
public class ResJsonObj
{
    private String id;

    private Result result;

    private String jsonrpc;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public Result getResult ()
    {
        return result;
    }

    public void setResult (Result result)
    {
        this.result = result;
    }

    public String getJsonrpc ()
    {
        return jsonrpc;
    }

    public void setJsonrpc (String jsonrpc)
    {
        this.jsonrpc = jsonrpc;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", result = "+result+", jsonrpc = "+jsonrpc+"]";
    }
}
