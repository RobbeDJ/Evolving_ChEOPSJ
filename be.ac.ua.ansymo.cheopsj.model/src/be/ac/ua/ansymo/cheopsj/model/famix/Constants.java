/*******************************************************************************
 * Copyright (c) 2011 Quinten David Soetens
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Quinten David Soetens - initial API and implementation
 ******************************************************************************/
package be.ac.ua.ansymo.cheopsj.model.famix;

public class Constants {
	/** Name of the default package. */
    public static final String DEFAULT_PACKAGE_NAME = "<DEFAULT PACKAGE>";

    /** Name of array types. */
    public static final String ARRAY_TYPE_NAME = "<Array>";

    /** Name of constructors. */
    public static final String CONSTRUCTOR_PREFIX = "<init>";

    /** Name of object initializer methods - each class contains one. */
    public static final String OBJECT_INIT_METHOD = "<oinit>()";

    /** Name of class initializer methods - each class contains one. */
    public static final String CLASS_INIT_METHOD = "<clinit>()";

    /** Java fModifiers (copied from the org.eclipse.jdt.core.dom.Modifier class) */
    public static final int MODIFIER_NONE = 0;

    /** The Constant MODIFIER_PUBLIC. */
    public static final int MODIFIER_PUBLIC = 1;

    /** The Constant MODIFIER_PRIVATE. */
    public static final int MODIFIER_PRIVATE = 2;

    /** The Constant MODIFIER_PROTECTED. */
    public static final int MODIFIER_PROTECTED = 4;

    /** The Constant MODIFIER_STATIC. */
    public static final int MODIFIER_STATIC = 8;

    /** The Constant MODIFIER_FINAL. */
    public static final int MODIFIER_FINAL = 16;

    /** The Constant MODIFIER_ABSTRACT. */
    public static final int MODIFIER_ABSTRACT = 1024;

    /** Additional modifier to mark an interface. */
    public static final int MODIFIER_INTERFACE = 16384;

    /** Additional modifier to mark enum. */
    public static final int MODIFIER_ENUM = 32768;

    /** Constant for an initializer block. */
    public static final int INIT_METHOD_MODIFIERS = 8; // 8 is the modifier integer of an initializer block

    /** Constant for unknown fModifiers. */
    public static final int UNKOWN_OR_NO_MODIFIERS = -1;

    /** Unique name delimiter. */
    public static final String NAME_DELIMITER = ".";

    /** Separator of anonymous classes. */
    public static final String ANONYMOUS_CLASS_SEPARATOR = "$";

    /**
     * Start of class parameter list.
     */
    public static final String CLASS_PARAMETER_START_BRACE = "<";
    /**
     * End of class parameter list.
     */
    public static final String CLASS_PARAMETER_END_BRACE = "<";
    /**
     * Separator of class parameters.
     */
    public static final String CLASS_PARAMETER_SEPARATOR = ",";
    
    /**
     * Start of the method argument list.
     */
    public static final String METHOD_START_BRACE = "(";
    /**
     * End of the method argument list.
     */
    public static final String METHOD_END_BRACE = ")";
    /**
     * Separator of method parameters.
     */
    public static final String METHOD_PARAMETER_SEPARATOR = ",";
}
