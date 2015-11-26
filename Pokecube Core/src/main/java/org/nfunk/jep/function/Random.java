/*****************************************************************************

JEP - Java Math Expression Parser 2.3.1
      January 26 2006
      (c) Copyright 2004, Nathan Funk and Richard Morris
      See LICENSE.txt for license information.

*****************************************************************************/
package org.nfunk.jep.function;

import java.util.Stack;

import org.nfunk.jep.ParseException;
/**
* Encapsulates the Math.random() function.
*/
public class Random extends PostfixMathCommand
{
	public Random()
	{
		numberOfParameters = 0;

	}

	@Override
	public void run(Stack inStack)
		throws ParseException
	{
		checkStack(inStack);// check the stack
		inStack.push(new Double(Math.random()));
		return;
	}
}
