/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package net.mindengine.galen.tests.runner;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.mindengine.galen.GalenMain;
import net.mindengine.galen.runner.GalenArguments;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.google.common.io.Files;

public class GalenMainTest {


    @Test public void shouldRun_singleTestSuccessfully() throws IOException {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        System.setProperty("url", testUrl);
        System.setProperty("spec.path", getClass().getResource("/html/page.spec").getFile());
        
        GalenMain galen = new GalenMain();
        
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath() + "/report.html";
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        galen.execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/suites/to-run/suite-single.test").getFile()))
            .withHtmlReport(htmlReportPath)
            .withTestngReport(testngReportPath)
            );
        
        assertThat("Should create screenshot 1 and place it in same folder as report", new File(reportsDir.getAbsolutePath() + "/screenshot-1.png").exists(), is(true));
        assertThat("Should create screenshot 2 and place it in same folder as report", new File(reportsDir.getAbsolutePath() + "/screenshot-2.png").exists(), is(true));
        
        String htmlReportContent = FileUtils.readFileToString(new File(htmlReportPath));
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
        
        //Verifying only parts of the report content to make sure that the test were executed
        assertThat(htmlReportContent, containsString("<h1>Home page test</h1>"));
        assertThat(htmlReportContent, containsString("<h2>" + testUrl + " 640x480</h2>"));
        assertThat(htmlReportContent, containsString("<h1>Home page test 2</h1>"));
        assertThat(htmlReportContent, containsString("<h2>" + testUrl + " 320x600</h2>"));
        
        assertThat(testngReportContent, containsString("<suite name=\"Home page test\">"));
        assertThat(testngReportContent, containsString("<test name=\"" + testUrl + " 640x480\">"));
        
        assertThat(testngReportContent, containsString("<suite name=\"Home page test 2\">"));
        assertThat(testngReportContent, containsString("<test name=\"" + testUrl + " 320x600\">"));
    }
    
    
    @Test public void shouldFindAndRun_allTestsRecursivelly() throws IOException {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        System.setProperty("url", testUrl);
        System.setProperty("spec.path", getClass().getResource("/html/page.spec").getFile());
        
        GalenMain galen = new GalenMain();
        
        File reportsDir = Files.createTempDir();
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        galen.execute(new GalenArguments()
            .withAction("test")
            .withPaths(asList(getClass().getResource("/suites/to-run/recursive-check").getFile()))
            .withRecursive(true)
            .withTestngReport(testngReportPath)
            );
        
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
        
        assertThat(testngReportContent, containsString("<suite name=\"Recursion check 1\">"));
        assertThat(testngReportContent, containsString("<test name=\"" + testUrl + " 640x480\">"));
        
        assertThat(testngReportContent, containsString("<suite name=\"Recursion check 2\">"));
        assertThat(testngReportContent, containsString("<test name=\"" + testUrl + " 320x480\">"));
        
        assertThat(testngReportContent, containsString("<suite name=\"Recursion check 3\">"));
        assertThat(testngReportContent, containsString("<test name=\"" + testUrl + " 640x480\">"));
    }
    
    @Test public void shouldRun_simplePageCheck() throws IOException {
        String testUrl = "file://" + getClass().getResource("/html/page-nice.html").getFile();
        String pageSpec = getClass().getResource("/html/page.spec").getFile();
        File reportsDir = Files.createTempDir();
        String htmlReportPath = reportsDir.getAbsolutePath() + "/report.html";
        String testngReportPath = reportsDir.getAbsolutePath() + "/testng-report.html";
        
        new GalenMain().execute(new GalenArguments()
            .withAction("check")
            .withUrl(testUrl)
            .withPaths(Arrays.asList(pageSpec))
            .withScreenSize(new Dimension(450, 500))
            .withHtmlReport(htmlReportPath)
            .withTestngReport(testngReportPath)
            .withIncludedTags("desktop"));
        
        assertThat("Should create screenshot 1 and place it in same folder as report", new File(reportsDir.getAbsolutePath() + "/screenshot-1.png").exists(), is(true));
        
        String htmlReportContent = FileUtils.readFileToString(new File(htmlReportPath));
        String testngReportContent = FileUtils.readFileToString(new File(testngReportPath));
        
        //Verifying only parts of the report content to make sure that the test were executed
        assertThat(htmlReportContent, containsString("<h1>" + pageSpec + "</h1>"));
        assertThat(htmlReportContent, containsString("<h2>" + testUrl + " 450x500</h2>"));
        
        assertThat(testngReportContent, containsString("<suite name=\"" + pageSpec + "\">"));
        assertThat(testngReportContent, containsString("<test name=\"" + testUrl + " 450x500\">"));
    }
}
