/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.surveydesign;

/**
 * Created by iansigmon on 2/2/17.
 */

/**
 * Exception thrown if survey design metadata is invalid
 */
public class InvalidDesignException extends Exception
{
    public InvalidDesignException(String message)
    {
        this(message, null);
    }

    public InvalidDesignException(String message, Throwable inner)
    {
        super(message, inner);
    }
}
