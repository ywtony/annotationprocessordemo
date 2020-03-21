package com.yw.butterknife_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.yw.butterknife_annotations.BindView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {
    private Elements elements;
    private Messager messager;
    private Filer filer;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
    }

    /**
     * 返回processor可处理的注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(BindView.class.getCanonicalName());
        return sets;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    /**
     * 注解处理器的核心方法，在这里来处理注解，并生成Java辅助类
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //通过RoundEnvironment扫描所有的注解文件，并获取所有的注解字段
        Map<TypeElement, List<FieldViewBinding>> targetMap = getTargetClassMap(roundEnvironment);

        //生成Java文件
        createJavaFile(targetMap.entrySet());
        return false;
    }

    private Map<TypeElement, List<FieldViewBinding>> getTargetClassMap(RoundEnvironment roundEnvironment) {
        //key=activity，value=activity中所有被注解修饰过的字段
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();

        //获取包含指定注解的类
        Set<? extends Element> annotationElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : annotationElements) {
            //获取字段名称
            String fieldName = element.getSimpleName().toString();
            //获取字段类型
            TypeMirror typeMirror = element.asType();
            //获取注解的值
            int id = element.getAnnotation(BindView.class).value();
            //获取element的全限定名
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> list = targetMap.get(typeElement);
            if (list == null) {
                list = new ArrayList<>();
                targetMap.put(typeElement, list);
            }
            list.add(new FieldViewBinding(fieldName, typeMirror, id));
        }
        return targetMap;

    }

    //利用JavaPoet创建辅助文件
    private void createJavaFile(Set<Map.Entry<TypeElement, List<FieldViewBinding>>> entries) {
        for (Map.Entry<TypeElement, List<FieldViewBinding>> entry : entries) {
            TypeElement typeElement = entry.getKey();
            List<FieldViewBinding> list = entry.getValue();
            if (list == null || list.size() == 0) {
                continue;
            }
            //获取类的包名
            String packageName = elements.getPackageOf(typeElement).getQualifiedName().toString();
            //创建Java文件
            String className = typeElement.getQualifiedName().toString().substring(packageName.length() + 1);
            //新类名，后面加上一个_ViewBinding用以区分
            String newClassName = className + "_ViewBinding";
            //javapoet中的类
            MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder().
                    addModifiers(Modifier.PUBLIC)//添加公共构造函数
                    .addParameter(ClassName.bestGuess(className), "target");//添加参数
            for (FieldViewBinding fieldViewBinding : list) {
                //获取类的全名
                String packageNameString = fieldViewBinding.getTypeMirror().toString();
                ClassName viewClass = ClassName.bestGuess(packageNameString);
                methodBuilder.addStatement("target.$L=($L)target.findViewById($L)",
                        fieldViewBinding.getFieldName(), viewClass, fieldViewBinding.getViewId());

            }
            TypeSpec typeSpec = TypeSpec.classBuilder(newClassName).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                    .addFileComment("Generated code from Butter Knife. Do not modify!")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
