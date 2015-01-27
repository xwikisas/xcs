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
package com.xwikisas.xcs.wikiflavor;

import org.xwiki.stability.Unstable;

/**
 * A flavor is an extension so will be used as the main extension of a subwiki.
 *
 * @version $Id: $
 * @since 2015-1-M1
 */
@Unstable
public class Flavor
{
    private String extensionId;

    private String name;

    private String nameTranslationKey;

    private String descriptionTranslationKey;

    private String icon;

    /**
     * Construct a new Flavor.
     *
     * @param extensionId id of the main extension
     * @param name technical name of the flavor
     * @param nameTranslationKey translation key for the name of the flavor
     * @param descriptionTranslationKey translation key for the description of the flavor
     * @param icon the icon to use top represent the flavor
     */
    public Flavor(String extensionId, String name, String nameTranslationKey, String descriptionTranslationKey,
            String icon)
    {
        this.extensionId = extensionId;
        this.name = name;
        this.nameTranslationKey = nameTranslationKey;
        this.descriptionTranslationKey = descriptionTranslationKey;
        this.icon = icon;
    }

    /**
     * @return the extension id
     */
    public String getExtensionId()
    {
        return extensionId;
    }

    /**
     * @return the technical name of the flavor
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the translation key for the name of the flavor
     */
    public String getNameTranslationKey()
    {
        return nameTranslationKey;
    }

    /**
     * @return the translation key for the description of the flavor
     */
    public String getDescriptionTranslationKey()
    {
        return descriptionTranslationKey;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }
}
