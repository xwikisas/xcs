/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwikisas.xcs.wikiflavour;

import org.xwiki.stability.Unstable;

/**
 * A flavour is an extension so will be used as the main extension of a subwiki.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Unstable
public class Flavour
{
    private String extensionId;

    private String name;

    private String nameTranslationKey;

    /**
     * Construct a new Flavour.
     *
     * @param extensionId id of the main extension
     * @param name technical name of the flavour
     * @param nameTranslationKey translation key for the name of the flavour
     */
    public Flavour(String extensionId, String name, String nameTranslationKey)
    {
        this.extensionId = extensionId;
        this.name = name;
        this.nameTranslationKey = nameTranslationKey;
    }

    /**
     * @return the extension id
     */
    public String getExtensionId()
    {
        return extensionId;
    }

    /**
     * @return the technical name of the flavour
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the translation key for the name of the flavour
     */
    public String getNameTranslationKey()
    {
        return nameTranslationKey;
    }
}
