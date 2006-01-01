/*
 * $Id$
 *
 * JNode.org
 * Copyright (C) 2003-2006 JNode.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package org.jnode.vm.memmgr.def;

import org.jnode.vm.memmgr.GCStatistics;

/**
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
final class DefGCStatistics extends GCStatistics {

    long lastGCTime;
    long lastMarkDuration;
    int lastMarkIterations;
    long lastSweepDuration;
    long lastCleanupDuration;
    long lastVerifyDuration;
    long lastFreedBytes;
    long lastMarkedObjects;

    public String toString() {
        return "lastGCTime          " + lastGCTime + "\n" +
               "lastMarkIterations  " + lastMarkIterations + "\n" +
               "lastMarkDuration    " + lastMarkDuration + "\n" +
               "lastSweepDuration   " + lastSweepDuration + "\n" +
               "lastCleanupDuration " + lastCleanupDuration + "\n" +
               "lastVerifyDuration  " + lastVerifyDuration + "\n" +
               "lastMarkedObjects   " + lastMarkedObjects + "\n" +
               "lastFreedBytes      " + lastFreedBytes;
    }
    
}
