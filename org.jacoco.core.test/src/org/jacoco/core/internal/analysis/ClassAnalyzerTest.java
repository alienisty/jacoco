/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.internal.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.jacoco.core.internal.instr.InstrSupport;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

/**
 * Unit tests for {@link ClassAnalyzer}.
 */
public class ClassAnalyzerTest {

	private ClassAnalyzer analyzer;
	private ClassCoverageImpl coverage;

	@Before
	public void setup() {
		coverage = new ClassCoverageImpl("Foo", 0x0000, false);
		analyzer = new ClassAnalyzer(coverage, null, new StringPool());
		analyzer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "Foo", null,
				"java/lang/Object", null);
	}

	@Test(expected = IllegalStateException.class)
	public void testAnalyzeInstrumentedClass1() {
		analyzer.visitField(InstrSupport.DATAFIELD_ACC,
				InstrSupport.DATAFIELD_NAME, InstrSupport.DATAFIELD_DESC, null,
				null);
	}

	@Test(expected = IllegalStateException.class)
	public void testAnalyzeInstrumentedClass2() {
		analyzer.visitMethod(InstrSupport.INITMETHOD_ACC,
				InstrSupport.INITMETHOD_NAME, InstrSupport.INITMETHOD_DESC,
				null, null);
	}

	@Test
	public void testMethodFilter_Empty() {
		final MethodProbesVisitor mv = analyzer.visitMethod(0, "foo", "()V",
				null, null);
		mv.visitEnd();
		assertEquals(0, coverage.getMethods().size());
	}

	@Test
	public void testMethodFilter_NonSynthetic() {
		final MethodProbesVisitor mv = analyzer.visitMethod(0, "foo", "()V",
				null, null);
		mv.visitCode();
		mv.visitInsn(Opcodes.RETURN);
		mv.visitEnd();
		assertEquals(1, coverage.getMethods().size());
	}

	@Test
	public void testMethodFilter_Synthetic() {
		final MethodProbesVisitor mv = analyzer.visitMethod(
				Opcodes.ACC_SYNTHETIC, "foo", "()V", null, null);
		assertNull(mv);
		assertTrue(coverage.getMethods().isEmpty());
	}

	@Test
	public void testMethodFilter_Lambda() {
		final MethodProbesVisitor mv = analyzer.visitMethod(
				Opcodes.ACC_SYNTHETIC, "lambda$1", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(Opcodes.RETURN);
		mv.visitEnd();
		assertEquals(1, coverage.getMethods().size());
	}

	@Test
	public void testMethodFilter_EnumValues() {
		analyzer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "Foo", null,
				"java/lang/Enum", null);
		final MethodProbesVisitor mv = analyzer.visitMethod(0, "values",
				"()[LFoo;", null, null);
		assertNull(mv);
		assertTrue(coverage.getMethods().isEmpty());
	}

	@Test
	public void testMethodFilter_EnumNonValues() {
		analyzer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "Foo", null,
				"java/lang/Enum", null);
		final MethodProbesVisitor mv = analyzer.visitMethod(0, "values", "()V",
				null, null);
		mv.visitCode();
		mv.visitInsn(Opcodes.RETURN);
		mv.visitEnd();
		assertEquals(1, coverage.getMethods().size());
	}

	@Test
	public void testMethodFilter_EnumValueOf() {
		analyzer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "Foo", null,
				"java/lang/Enum", null);
		final MethodProbesVisitor mv = analyzer.visitMethod(0, "valueOf",
				"(Ljava/lang/String;)LFoo;", null, null);
		assertNull(mv);
		assertTrue(coverage.getMethods().isEmpty());
	}

	@Test
	public void testMethodFilter_EnumNonValueOf() {
		analyzer.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC, "Foo", null,
				"java/lang/Enum", null);
		final MethodProbesVisitor mv = analyzer.visitMethod(0, "valueOf", "()V",
				null, null);
		mv.visitCode();
		mv.visitInsn(Opcodes.RETURN);
		mv.visitEnd();
		assertEquals(1, coverage.getMethods().size());
	}

}
