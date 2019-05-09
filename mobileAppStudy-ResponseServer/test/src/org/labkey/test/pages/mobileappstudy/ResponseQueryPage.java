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
package org.labkey.test.pages.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.components.ext4.Message;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ResponseQueryPage extends LabKeyPage
{
    private static final String REPROCESS_BUTTON = "Reprocess";

    public enum ColumnNames
    {
        ResponseId("Response Id"),
        ResponseJson("Response"),
        ParticipantId("Participant Id"),
        AppToken("App Token"),
        SurveyVersion("Survey Version"),
        ActivityId("Activity Id"),
        Processed("Processed"),
        ProcessedBy("Processed By"),
        Error("Error Message"),
        Status("Status");

        private String _columnLabel;

        public String getLabel()
        {
            return _columnLabel;
        }

        ColumnNames(String displayText)
        {
            _columnLabel = displayText;
        }
    }

    BaseWebDriverTest _test;

    public ResponseQueryPage(BaseWebDriverTest test)
    {
        super(test);
        _test = test;
    }

    public void viewResponseTable()
    {
        _test.goToSchemaBrowser();
        _test.viewQueryData("mobileappstudy","Response");
    }

    public void reprocessRows(int...rowIds)
    {
        DataRegionTable table = new DataRegionTable("query", getDriver());
        table.uncheckAll();
        for(int i : rowIds)
            table.checkCheckbox(i);

        clickButton(REPROCESS_BUTTON, 0);
    }

    public void assertSuccessfulReprocessAlert(int count, String...unsuccessfulIds)
    {
        SuccessfulReprocessingMessage msg = new SuccessfulReprocessingMessage(getDriver());
        msg.assertSuccessfulTextMessage(count, unsuccessfulIds);
        msg.clickOk();
    }

    public void filterByAppToken(String appToken)
    {
        DataRegionTable table = new DataRegionTable("query", getDriver());
        table.clearAllFilters(ColumnNames.AppToken.toString());
        table.setFilter(ColumnNames.AppToken.toString(), "Equals", appToken);
    }

    public void assertResponseErrorCounts(String appToken, int submissionCount, int successfulProcessingExpected)
    {
        //Go to Data table
        viewResponseTable();

        //Filter by AppToken
        filterByAppToken(appToken);

        DataRegionTable table = new DataRegionTable("query", getDriver());
        List statusValues = table.getColumnDataAsText(ColumnNames.Status.toString());
        assertEquals("Unexpected number of requests", submissionCount, statusValues.size());
        assertEquals("Unexpected number of successfully processed requests", successfulProcessingExpected, Collections.frequency(statusValues, "PROCESSED"));
        assertEquals("Unexpected number of unsuccessful requests", submissionCount - successfulProcessingExpected, Collections.frequency(statusValues, "ERROR"));
        table.clearAllFilters(ColumnNames.AppToken.toString());
    }

    public void assertResponseErrorCounts(String appToken, int submissionCount)
    {
        //Go to Data table
        viewResponseTable();

        //Filter by AppToken
        filterByAppToken(appToken);

        DataRegionTable table = new DataRegionTable("query", getDriver());
        List statusValues = table.getColumnDataAsText(ColumnNames.Status.toString());
        assertEquals("Unexpected number of requests", submissionCount, statusValues.size());
        assertEquals("Unexpected number of unsuccessful requests", submissionCount, Collections.frequency(statusValues, "ERROR"));
        table.clearAllFilters(ColumnNames.AppToken.toString());
    }

    public String getResponseId(int rowIndex)
    {
        DataRegionTable table = new DataRegionTable("query", getDriver());
        return table.getDataAsText(rowIndex, ColumnNames.ResponseId.toString());
    }

    public int getResponseCount()
    {
        DataRegionTable table = new DataRegionTable("query", getDriver());
        return table.getDataRowCount();
    }

    public Collection<String> getStatuses()
    {
        DataRegionTable table = new DataRegionTable("query", getDriver());
        return table.getColumnDataAsText(ColumnNames.Status.toString());
    }

    public class SuccessfulReprocessingMessage extends Message
    {
        public static final String WARNING_TITLE = "Reprocess Successful";
        private static final String SUCCESS_MESSAGE_FORMAT = "Reprocessed %1$d response%2$s.";
        private static final String NOT_PROCESSED_SINGULAR_FORMAT = "Response [ %1$s ] was not reprocessed, as it was successfully processed previously.";
        private static final String NOT_PROCESSED_PLURAL_FORMAT = "Responses [ %1$s ] were not reprocessed, as they were successfully processed previously.";

        public SuccessfulReprocessingMessage(WebDriver wd)
        {
            super(WARNING_TITLE, wd);
        }

        public void clickOk()
        {
            clickButton("OK", true);
        }

        private String getSuccessfulTextMessage(int count)
        {
            return String.format(SUCCESS_MESSAGE_FORMAT, count, count == 1 ? "" : "s");
        }

        private String getNotProcessedMessage(String[] ids)
        {
            Collection<String> unprocessedIds = ids != null ? Arrays.asList(ids) : Collections.emptyList();

            String unprocessedFormat = unprocessedIds.size() == 1 ? NOT_PROCESSED_SINGULAR_FORMAT : NOT_PROCESSED_PLURAL_FORMAT;
            return String.format(unprocessedFormat, String.join(", ", unprocessedIds));
        }

        public void assertSuccessfulTextMessage(int countSuccessful, String...unprocessedIds)
        {
            String expected = getSuccessfulTextMessage(countSuccessful) + (unprocessedIds.length > 0 ? "\n\n" + getNotProcessedMessage(unprocessedIds) : "");
            assertEquals("Reprocessing message not as expected.", expected, getMessage());
        }

        public String getMessage()
        {
            return getText(Locator.css("div.x4-message-box div.x4-form-display-field"));
        }
    }
}
