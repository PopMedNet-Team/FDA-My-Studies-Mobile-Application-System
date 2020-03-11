/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.labkey.test.tests.mobileappstudy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.categories.Git;
import org.labkey.test.components.mobileappstudy.TokenBatchPopup;
import org.labkey.test.pages.mobileappstudy.TokenBatchPage;
import org.labkey.test.pages.mobileappstudy.TokenListPage;

import java.util.Map;

@Category({Git.class})
public class EnrollmentTokensTest extends BaseMobileAppStudyTest
{
    @Override
    void setupProjects()
    {
        _containerHelper.createProject(getProjectName(), null);
        _containerHelper.enableModule("MobileAppStudy");
    }

    @Before
    public void preTest()
    {
        goToProjectHome();
    }

    @Test
    public void testEnableDisableButtons()
    {
        TokenBatchPage tokenBatchPage = TokenBatchPage.beginAt(this, getProjectName());
        tokenBatchPage.openNewBatchPopup();
        Assert.assertTrue("New Batch button on grid should be disabled", tokenBatchPage.isNewBatchEnabled());
        TokenBatchPopup popup = new TokenBatchPopup(getWrappedDriver());
        Assert.assertFalse("Submit button should not be enabled", popup.isSubmitEnabled());
        Assert.assertTrue("Cancel button should not be enabled", popup.isCancelEnabled());
        log("Selecting a valid batch size");
        popup.selectBatchSize("100");
        Assert.assertTrue("Submit button should be enabled", popup.isSubmitEnabled());
        popup.selectOtherBatchSize();
        Assert.assertFalse("Submit button should be disabled", popup.isSubmitEnabled());
        popup.setOtherBatchSize("4");
    }

    @Test
    public void testCreateNewBatch()
    {
        TokenBatchPage tokenBatchPage = TokenBatchPage.beginAt(this, getProjectName());
        Assert.assertTrue(tokenBatchPage.isNewBatchPresent());
        tokenBatchPage.openNewBatchPopup();
        TokenBatchPopup popup = new TokenBatchPopup(getWrappedDriver());
        popup.createNewBatch("100");
        TokenListPage tokenListPage = new TokenListPage(this);
        Assert.assertEquals("Number of tokens generated not as expected", 100, tokenListPage.getNumTokens());
        Integer rowId = Integer.valueOf(getUrlParam("enrollmentTokens.BatchId%2FRowId~eq"));
        log("Returning to token batches page");
        tokenListPage.goToBatches();
        Map<String, String> batchData = tokenBatchPage.getBatchData(rowId);
        Assert.assertEquals("Number of tokens not as expected", "100", batchData.get("Count"));
        Assert.assertEquals("Number of tokens in use not as expected", "0", batchData.get("TokensInUse"));
    }

    @Test
    public void testCancelNewBatch()
    {
        TokenBatchPage tokenBatchPage = TokenBatchPage.beginAt(this, getProjectName());
        tokenBatchPage.openNewBatchPopup();
        TokenBatchPopup popup = new TokenBatchPopup(getWrappedDriver());
        popup.selectBatchSize("1,000");
        popup.cancelNewBatch();
        Assert.assertTrue("New batch button should be enabled", tokenBatchPage.isNewBatchEnabled());
    }

    @Override
    protected String getProjectName()
    {
        return "EnrollmentTokensTest Project";
    }
}