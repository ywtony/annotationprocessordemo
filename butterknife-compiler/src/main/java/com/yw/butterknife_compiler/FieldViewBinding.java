package com.yw.butterknife_compiler;

import javax.lang.model.type.TypeMirror;

/**
 * create by yangwei
 * on 2020-01-04 22:35
 */
public class FieldViewBinding {
    private String fieldName;
    private TypeMirror typeMirror;
    private int viewId;

    public FieldViewBinding() {
    }

    public FieldViewBinding(String fieldName, TypeMirror typeMirror, int viewId) {
        this.fieldName = fieldName;
        this.typeMirror = typeMirror;
        this.viewId = viewId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public TypeMirror getTypeMirror() {
        return typeMirror;
    }

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public static void main(String[] args) {
        System.out.println("sldjaldfa");
    }
}
