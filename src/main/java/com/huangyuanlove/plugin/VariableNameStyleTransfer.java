package com.huangyuanlove.plugin;

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
import org.jetbrains.annotations.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class VariableNameStyleTransfer extends AnAction {
    private final Pattern linePattern = Pattern.compile("_(\\w)");
    private final Pattern humpPattern = Pattern.compile("[A-Z]");
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(e.getProject()!=null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
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
