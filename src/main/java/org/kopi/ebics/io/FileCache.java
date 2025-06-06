/*
 * Copyright (c) 1990-2012 kopiLeft Development SARL, Bizerte, Tunisia
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id$
 */

package org.kopi.ebics.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.Map;

/**
 * A simple mean to cache created files.
 *
 * @author hachani
 */
public class FileCache {

    private final Map<String, File> cache;
    private boolean isTraceEnabled;

    /**
     * Constructs a new <code>FileCache</code> object
     *
     * @param isTraceEnabled is trace enabled?
     */
    public FileCache(boolean isTraceEnabled) {
        this.isTraceEnabled = isTraceEnabled;
        cache = new Hashtable<>();
    }

    /**
     * Cache a new <code>java.io.File</code> in the cache buffer
     *
     * @param file the file to cache
     * @return True if the file is cached
     */
    public boolean add(File file) {
        if (cache.containsKey(file.getName())) {
            return false;
        }

        cache.put(file.getName(), file);

        return true;
    }

    /**
     * Removes the given <code>java.io.file</code> from the cache.
     *
     * @param filename the file to remove
     */
    public void remove(String filename) {
        if (!cache.containsKey(filename)) {
            return;
        }

        cache.remove(filename);
    }


    /**
     * Clears the cache buffer
     */
    public void clear() throws IOException {
        if (isTraceEnabled) {
            for (File file : cache.values()) {
                Files.delete(file.toPath());
            }
        }

        cache.clear();
    }

    /**
     * Sets the trace ability.
     *
     * @param enabled is trace enabled?
     */
    public void setTraceEnabled(boolean enabled) {
        this.isTraceEnabled = enabled;
    }
}
