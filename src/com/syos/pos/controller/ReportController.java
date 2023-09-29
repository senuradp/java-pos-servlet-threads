package com.syos.pos.controller;

import com.syos.pos.report.BillReport;
import com.syos.pos.report.SalesReport;
import com.syos.pos.report.ShelfReport;
import com.syos.pos.report.StockReport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportController extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("getSalesReport".equals(action)) {
            // Get the date parameter from the client
            String dateParam = request.getParameter("date");

            if (dateParam != null && !dateParam.isEmpty()) {
                try {
                    // Parse the date string into a Date object
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(dateParam);

                    // Create an instance of the SalesReport class
                    SalesReport salesReport = new SalesReport();

                    // Generate the Sales report for the given date
                    String report = salesReport.generateReportByDate(date);

                    // Send the report as a response to the client
                    response.setContentType("text/plain");
                    response.getWriter().write(report);
                } catch (Exception e) {
                    // Handle any exceptions
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Failed to generate the report: " + e.getMessage());
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date parameter is missing.");
            }
        }
        else if ("getShelfReport".equals(action)){
            // Get the date parameter from the client
            String dateParam = request.getParameter("date");

            if (dateParam != null && !dateParam.isEmpty()) {
                try {
                    // Parse the date string into a Date object
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(dateParam);

                    // Create an instance of the SalesReport class
                    ShelfReport shelfReport = new ShelfReport();

                    // Generate the Sales report for the given date
                    String report = shelfReport.generateReportByDate(date);

                    // Send the report as a response to the client
                    response.setContentType("text/plain");
                    response.getWriter().write(report);
                } catch (Exception e) {
                    // Handle any exceptions
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            "Failed to generate the report: " + e.getMessage());
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date parameter is missing.");
            }
        }
        else if ("getStockReport".equals(action)){
            try {
                // Create an instance of the SalesReport class
                StockReport stockReport = new StockReport();

                // Generate the Sales report for the given date
                String report = stockReport.generateReport();

                // Send the report as a response to the client
                response.setContentType("text/plain");
                response.getWriter().write(report);
            } catch (Exception e) {
                // Handle any exceptions
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to generate the report: " + e.getMessage());
            }
        }
        else if ("getBillReport".equals(action)){
            try {
                // Create an instance of the SalesReport class
                BillReport billReport = new BillReport();

                // Generate the Sales report for the given date
                String report = billReport.generateReport();

                // Send the report as a response to the client
                response.setContentType("text/plain");
                response.getWriter().write(report);
            } catch (Exception e) {
                // Handle any exceptions
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Failed to generate the report: " + e.getMessage());
            }
        }
        else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action.");
        }
    }

}
