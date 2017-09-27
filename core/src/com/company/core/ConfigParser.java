package com.company.core;

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.protobuf.*;
import com.google.protobuf.GeneratedMessageV3.Builder;

public class ConfigParser
{
    public <E, T extends Builder<T>> E Parse(String path, Builder<T> builder) throws Exception
    {
        StringBuffer fullText = new StringBuffer();
        if (Include(path, true, fullText))
        {
            _fullText = fullText;
            TextFormat.merge(fullText, builder);
            E protoConf = (E)builder.build();
            return protoConf;
        }
        return null;
    }

    public void SaveParsed(String outPath)
    {
        try(  PrintWriter out = new PrintWriter( outPath ))
        {
            out.println(_fullText);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void SaveOriginal(String outPath)
    {
        try(PrintWriter out = new PrintWriter(outPath))
        {
            out.println(_originalText );
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private boolean Include(String fileName, boolean isFullPath, StringBuffer fullText) throws Exception
    {
        AbstractConfigAccessor acc = ConfigAccessorFactory.CreateConfigAccessor(fileName);
        int lineNum = 0;
        String line;

        while((line = acc.GetNextLine()) != null)
        {
            if (isFullPath)
            {
                _originalText.append(line + '\n');
            }

            lineNum++;
            if (line.startsWith("@include"))
            {
                String[] parts = line.split("\\s+");
                String inclPath = String.format("%s/%s", acc.GetCfgLocation(), parts[1].trim());
                if (!Include(inclPath, false, fullText))
                {
                    // log error fileName and lineNum
                    return false;
                }
                continue;
            }

            String newLine;
            if ((newLine = ProcessString(line)) != null)
            {
                // log error fileName and lineNum
                fullText.append(newLine + '\n');
            }
        }
        return true;
    }

    private String ProcessString(String line)
    {
        Pattern reg = Pattern.compile("(\\$\\(([^)]+)\\))");
        Matcher m = reg.matcher(line);
        while (m.find())
        {
            String varName = m.group(2);
            String rep = m.group(1);
            boolean isPresent = false;
            String val = "";

            if (_vars.containsKey(varName))
            {
                val = _vars.get(varName);
                isPresent = true;
            }
            else if (varName.startsWith("env."))
            {
                String envStr = System.getenv(varName.substring(4));
                if (envStr != null)
                {
                    isPresent = true;
                    val = envStr;
                }
            }

            if (!isPresent)
            {
                return null;
            }
            line.replaceAll(rep, val);
        }
        return line;
    }

    private StringBuffer _fullText = new StringBuffer();
    private StringBuffer _originalText = new StringBuffer();
    private HashMap<String, String> _vars = new HashMap<String, String>() ;
}
