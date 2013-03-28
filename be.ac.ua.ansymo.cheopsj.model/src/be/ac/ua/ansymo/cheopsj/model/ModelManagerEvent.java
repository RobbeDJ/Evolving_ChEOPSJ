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
package be.ac.ua.ansymo.cheopsj.model;

import java.util.EventObject;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

public class ModelManagerEvent extends EventObject{
   private static final long serialVersionUID = 3697053173951102953L;

   private final IChange[] added;

   public ModelManagerEvent(ModelManager source, IChange[] newChange) {
      super(source);
      added = newChange;
   }

   public IChange[] getNewChanges() {
      return added;
   }
}