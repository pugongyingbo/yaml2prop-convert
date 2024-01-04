package com.zzb.yaml2propconvert;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.Messages;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

/**
 * @author zhangzhaobo
 * @version 1.0
 * @description:
 * @date 2024/1/4 09:02
 */
public class ConvertYamlToPropertiesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取当前编辑器
        Editor editor = (Editor) e.getDataContext().getData("editor");
        if (editor == null) {
            Messages.showMessageDialog("No editor selected", "Error", Messages.getErrorIcon());
            return;
        }

        // 获取选定的文本内容
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showMessageDialog("No text selected", "Error", Messages.getErrorIcon());
            return;
        }

        if (!isYamlFormat(selectedText)) {
            Messages.showMessageDialog("Selected text is not in YAML format", "Error", Messages.getErrorIcon());
            return;
        }

        // 将 YAML 转换为 Properties 格式
        String convertedPropertiesContent = "";
        try {
            convertedPropertiesContent = convertYamlToProperties(selectedText);
        } catch (Exception ex) {
            Messages.showMessageDialog("Selected text is not in YAML format", "Error", Messages.getErrorIcon());
            ex.printStackTrace();
            return;
        }

        // 将转换后的内容放入剪贴板
        StringSelection selection = new StringSelection(convertedPropertiesContent);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        Messages.showInfoMessage("Already copy, please paste", "Success");

    }

    private boolean isYamlFormat(String text) {
        Yaml yaml = new Yaml();
        try {
            yaml.load(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String convertYamlToProperties(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(yamlContent);

        StringBuilder propertiesContent = new StringBuilder();
        try {
            parseYamlMap("", yamlMap, propertiesContent);
        } catch (Exception e) {
            throw e;
        }
        return propertiesContent.toString();
    }

    private void parseYamlMap(String prefix, Map<String, Object> map, StringBuilder propertiesContent) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                parseYamlMap(prefix + key + ".", (Map<String, Object>) value, propertiesContent);
            } else {
                propertiesContent.append(prefix).append(key).append("=").append(value).append("\n");
            }
        }
    }

}
