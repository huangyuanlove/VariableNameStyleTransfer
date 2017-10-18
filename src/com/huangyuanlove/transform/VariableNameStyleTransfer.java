package com.huangyuanlove.transform;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import org.apache.http.util.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangyuan on 2017/10/18.
 */
public class VariableNameStyleTransfer extends AnAction {
    private Pattern linePattern = Pattern.compile("_(\\w)");
    private Pattern humpPattern = Pattern.compile("[A-Z]");
    @Override
    public void actionPerformed(AnActionEvent e) {

        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        if (null == editor || null == project) {
            return;
        }
        final Document document = editor.getDocument();
        SelectionModel selectionModel = editor.getSelectionModel();
        final int selectedStart = selectionModel.getSelectionStart();
        final int selectedEnd = selectionModel.getSelectionEnd();
        String selectedText = selectionModel.getSelectedText();
        if (TextUtils.isBlank(selectedText)) {

        } else {
            String replaceText;
            if(selectedText.contains("_")){
                replaceText = lineToHump(selectedText);
            }else{
                replaceText = humpToLine(selectedText);
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.replaceString(selectedStart, selectedEnd, replaceText);
            });
            selectionModel.removeSelection();
        }

    }
    private String lineToHump (String str){
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    private String humpToLine(String str){
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
