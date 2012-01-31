/*
 * Created 2007/06/24
 * Copyright (C) 2003-2007  Naoki Iwami (naoki@limy.org)
 *
 * This file is part of Limy Eclipse Plugin.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.limy.eclipse.qalab.action.part;

import java.io.File;

import net.objectlab.qalab.ant.BuildStatMoverTask;

import org.apache.tools.ant.Project;
import org.eclipse.core.runtime.CoreException;
import org.limy.eclipse.qalab.AntCreator;
import org.limy.eclipse.qalab.LimyQalabConstants;
import org.limy.eclipse.qalab.ant.LimyCreatorUtils;
import org.limy.xml.VmParam;

/**
 * @author Naoki Iwami
 */
public class CalcQalabMoverAction extends AbstractPartAction {

    @Override
    protected String[] getTargetNames() {
        return new String[] {};
    }

    @Override
    protected void makeReport() throws CoreException {
        
        BuildStatMoverTask task = new BuildStatMoverTask();
        task.setProject(new Project());
        task.setInputFile(getQalabFile());
        AntCreator[] creators = LimyCreatorUtils.decideCreators(getEnv());
        task.setTypes(LimyCreatorUtils.createTypeString(creators, 0, 1));
        task.setStartTimeHoursOffset("720");
        task.setWeekendAdjustment(true);
        task.setOutputXMLfile(getDestFile("qalab-mover.xml"));
        task.execute();
        
        if (getDestFile("qalab-mover.xml").exists()) {
            // èââÒé¿çséûÇÕMoverÇÕë∂ç›ÇµÇ»Ç¢
            if (getEnv().getStore().getBoolean(
                    LimyQalabConstants.ENABLE_INDIVISUAL)) {
                outputReport("qalab-mover/index.vm", getDestFile("qalab-mover.xml"),
                        "qalab-mover.html", new VmParam("enable_indivisual", "on"));
            } else {
                outputReport("qalab-mover/index.vm", getDestFile("qalab-mover.xml"),
                        "qalab-mover.html");
            }
        }
    }

    @Override
    protected File getReportHtml() {
        return getDestFile("qalab-mover.html");
    }

    // ------------------------ Private Methods

}
