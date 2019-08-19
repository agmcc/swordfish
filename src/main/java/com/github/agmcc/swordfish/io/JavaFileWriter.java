package com.github.agmcc.swordfish.io;

import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

public class JavaFileWriter {

  private final Filer filer;

  public JavaFileWriter(final Filer filer) {
    this.filer = filer;
  }

  public void writeJavaFile(final String fileName, final JavaFile javaFile) {
    try {
      final JavaFileObject jfo = filer.createSourceFile(fileName);
      try (final Writer writer = jfo.openWriter()) {
        javaFile.writeTo(writer);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
