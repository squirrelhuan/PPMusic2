package com.example.ppmusic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcHandle {

	    private List mWords = new ArrayList();
	    private List mTimeList = new ArrayList();

	    //�������ļ�
	    public void readLRC(String path) {
	        File file = new File(path);

	        try {
	            FileInputStream fileInputStream = new FileInputStream(file);
	            InputStreamReader inputStreamReader = new InputStreamReader(
	                    fileInputStream, "utf-8");
	            BufferedReader bufferedReader = new BufferedReader(
	                    inputStreamReader);
	            String s = "";
	            while ((s = bufferedReader.readLine()) != null) {
	                addTimeToList(s);
	                if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1)
	                        || (s.indexOf("[by:") != -1)) {
	                    s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
	                } else {
	                    String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);
	                    s = s.replace(ss, "");
	                }
	                mWords.add(s);
	            }

	            bufferedReader.close();
	            inputStreamReader.close();
	            fileInputStream.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	            mWords.add("û�и���ļ����Ͻ�ȥ����");
	        } catch (IOException e) {
	            e.printStackTrace();
	            mWords.add("û�ж�ȡ�����");
	        }
	    }
	   public List getWords() {
	        return mWords;
	   }

	    public List getTime() {
	        return mTimeList;
	    }

	    // �����ʱ��
	    private int timeHandler(String string) {
	       string = string.replace(".", ":");
	       String timeData[] = string.split(":");
	// ������֡��벢ת��Ϊ����
	        int minute = Integer.parseInt(timeData[0]);
	        int second = Integer.parseInt(timeData[1]);
	        int millisecond = Integer.parseInt(timeData[2]);

	        // ������һ������һ�е�ʱ��ת��Ϊ������
	        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;

	        return currentTime;
	    }

	   private void addTimeToList(String string) {
	        Matcher matcher = Pattern.compile(
	                "\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
	        if (matcher.find()) {
	            String str = matcher.group();
	            mTimeList.add(new LrcHandle().timeHandler(str.substring(1,
	                    str.length() - 1)));
	        }

	    }
	}

