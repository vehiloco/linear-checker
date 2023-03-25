package org.checkerframework.checker.linear;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.SupportedLintOptions;
import org.checkerframework.framework.source.SupportedOptions;

@SupportedOptions({"atomotans"})
@SupportedLintOptions({"strongboxbacked"})
public class LinearChecker extends BaseTypeChecker {}
