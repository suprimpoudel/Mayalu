package com.np.suprimpoudel.mayalu.features.shared.helper

class EmailTemplate {
    companion object {
        fun getWelcomeUserTemplate(otpCode: String): String {
            return "\n" +
                    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
                    "<html data-editor-version=\"2\" class=\"sg-campaigns\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                    "    <head>\n" +
                    "      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                    "      <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1\">\n" +
                    "      <!--[if !mso]><!-->\n" +
                    "      <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">\n" +
                    "      <!--<![endif]-->\n" +
                    "      <!--[if (gte mso 9)|(IE)]>\n" +
                    "      <xml>\n" +
                    "        <o:OfficeDocumentSettings>\n" +
                    "          <o:AllowPNG/>\n" +
                    "          <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                    "        </o:OfficeDocumentSettings>\n" +
                    "      </xml>\n" +
                    "      <![endif]-->\n" +
                    "      <!--[if (gte mso 9)|(IE)]>\n" +
                    "  <style type=\"text/css\">\n" +
                    "    body {width: 600px;margin: 0 auto;}\n" +
                    "    table {border-collapse: collapse;}\n" +
                    "    table, td {mso-table-lspace: 0pt;mso-table-rspace: 0pt;}\n" +
                    "    img {-ms-interpolation-mode: bicubic;}\n" +
                    "  </style>\n" +
                    "<![endif]-->\n" +
                    "      <style type=\"text/css\">\n" +
                    "    body, p, div {\n" +
                    "      font-family: inherit;\n" +
                    "      font-size: 14px;\n" +
                    "    }\n" +
                    "    body {\n" +
                    "      color: #000000;\n" +
                    "    }\n" +
                    "    body a {\n" +
                    "      color: #1188E6;\n" +
                    "      text-decoration: none;\n" +
                    "    }\n" +
                    "    p { margin: 0; padding: 0; }\n" +
                    "    table.wrapper {\n" +
                    "      width:100% !important;\n" +
                    "      table-layout: fixed;\n" +
                    "      -webkit-font-smoothing: antialiased;\n" +
                    "      -webkit-text-size-adjust: 100%;\n" +
                    "      -moz-text-size-adjust: 100%;\n" +
                    "      -ms-text-size-adjust: 100%;\n" +
                    "    }\n" +
                    "    img.max-width {\n" +
                    "      max-width: 100% !important;\n" +
                    "    }\n" +
                    "    .column.of-2 {\n" +
                    "      width: 50%;\n" +
                    "    }\n" +
                    "    .column.of-3 {\n" +
                    "      width: 33.333%;\n" +
                    "    }\n" +
                    "    .column.of-4 {\n" +
                    "      width: 25%;\n" +
                    "    }\n" +
                    "    ul ul ul ul  {\n" +
                    "      list-style-type: disc !important;\n" +
                    "    }\n" +
                    "    ol ol {\n" +
                    "      list-style-type: lower-roman !important;\n" +
                    "    }\n" +
                    "    ol ol ol {\n" +
                    "      list-style-type: lower-latin !important;\n" +
                    "    }\n" +
                    "    ol ol ol ol {\n" +
                    "      list-style-type: decimal !important;\n" +
                    "    }\n" +
                    "    @media screen and (max-width:480px) {\n" +
                    "      .preheader .rightColumnContent,\n" +
                    "      .footer .rightColumnContent {\n" +
                    "        text-align: left !important;\n" +
                    "      }\n" +
                    "      .preheader .rightColumnContent div,\n" +
                    "      .preheader .rightColumnContent span,\n" +
                    "      .footer .rightColumnContent div,\n" +
                    "      .footer .rightColumnContent span {\n" +
                    "        text-align: left !important;\n" +
                    "      }\n" +
                    "      .preheader .rightColumnContent,\n" +
                    "      .preheader .leftColumnContent {\n" +
                    "        font-size: 80% !important;\n" +
                    "        padding: 5px 0;\n" +
                    "      }\n" +
                    "      table.wrapper-mobile {\n" +
                    "        width: 100% !important;\n" +
                    "        table-layout: fixed;\n" +
                    "      }\n" +
                    "      img.max-width {\n" +
                    "        height: auto !important;\n" +
                    "        max-width: 100% !important;\n" +
                    "      }\n" +
                    "      a.bulletproof-button {\n" +
                    "        display: block !important;\n" +
                    "        width: auto !important;\n" +
                    "        font-size: 80%;\n" +
                    "        padding-left: 0 !important;\n" +
                    "        padding-right: 0 !important;\n" +
                    "      }\n" +
                    "      .columns {\n" +
                    "        width: 100% !important;\n" +
                    "      }\n" +
                    "      .column {\n" +
                    "        display: block !important;\n" +
                    "        width: 100% !important;\n" +
                    "        padding-left: 0 !important;\n" +
                    "        padding-right: 0 !important;\n" +
                    "        margin-left: 0 !important;\n" +
                    "        margin-right: 0 !important;\n" +
                    "      }\n" +
                    "      .social-icon-column {\n" +
                    "        display: inline-block !important;\n" +
                    "      }\n" +
                    "    }\n" +
                    "  </style>\n" +
                    "    <style>\n" +
                    "      @media screen and (max-width:480px) {\n" +
                    "        table\\0 {\n" +
                    "          width: 480px !important;\n" +
                    "          }\n" +
                    "      }\n" +
                    "    </style>\n" +
                    "      <!--user entered Head Start--><link href=\"https://fonts.googleapis.com/css?family=Muli&display=swap\" rel=\"stylesheet\"><style>\n" +
                    "body {font-family: 'Muli', sans-serif;}\n" +
                    "</style><!--End Head user entered-->\n" +
                    "    </head>\n" +
                    "    <body>\n" +
                    "      <center class=\"wrapper\" data-link-color=\"#1188E6\" data-body-style=\"font-size:14px; font-family:inherit; color:#000000; background-color:#FFFFFF;\">\n" +
                    "        <div class=\"webkit\">\n" +
                    "          <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"wrapper\" bgcolor=\"#FFFFFF\">\n" +
                    "            <tr>\n" +
                    "              <td valign=\"top\" bgcolor=\"#FFFFFF\" width=\"100%\">\n" +
                    "                <table width=\"100%\" role=\"content-container\" class=\"outer\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                    "                  <tr>\n" +
                    "                    <td width=\"100%\">\n" +
                    "                      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                    "                        <tr>\n" +
                    "                          <td>\n" +
                    "                            <!--[if mso]>\n" +
                    "    <center>\n" +
                    "    <table><tr><td width=\"600\">\n" +
                    "  <![endif]-->\n" +
                    "                                    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:100%; max-width:600px;\" align=\"center\">\n" +
                    "                                      <tr>\n" +
                    "                                        <td role=\"modules-container\" style=\"padding:0px 0px 0px 0px; color:#000000; text-align:left;\" bgcolor=\"#FFFFFF\" width=\"100%\" align=\"left\"><table class=\"module preheader preheader-hide\" role=\"module\" data-type=\"preheader\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"display: none !important; mso-hide: all; visibility: hidden; opacity: 0; color: transparent; height: 0; width: 0;\">\n" +
                    "    <tr>\n" +
                    "      <td role=\"module-content\">\n" +
                    "        <p></p>\n" +
                    "      </td>\n" +
                    "    </tr>\n" +
                    "  </table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"100%\" role=\"module\" data-type=\"columns\" style=\"padding:30px 20px 30px 20px;\" bgcolor=\"#f6f6f6\" data-distribution=\"1\">\n" +
                    "    <tbody>\n" +
                    "      <tr role=\"module-content\">\n" +
                    "        <td height=\"100%\" valign=\"top\"><table width=\"540\" style=\"width:540px; border-spacing:0; border-collapse:collapse; margin:0px 10px 0px 10px;\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" border=\"0\" bgcolor=\"\" class=\"column column-0\">\n" +
                    "      <tbody>\n" +
                    "        <tr>\n" +
                    "          <td style=\"padding:0px;margin:0px;border-spacing:0;\"><table class=\"wrapper\" role=\"module\" data-type=\"image\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"1f11b0a6-c27f-417c-9d2f-9b464eabe056\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td style=\"font-size:6px; line-height:10px; padding:0px 0px 0px 0px;\" valign=\"top\" align=\"center\">\n" +
                    "          <img class=\"max-width\" border=\"0\" style=\"display:block; color:#000000; text-decoration:none; font-family:Helvetica, arial, sans-serif; font-size:16px; max-width:39% !important; width:39%; height:auto !important;\" width=\"211\" alt=\"Mayalu App Icon\" data-proportionally-constrained=\"true\" data-responsive=\"true\" src=\"http://cdn.mcauto-images-production.sendgrid.net/3d0219657258bdd2/b91c7424-bb49-4ea3-81ff-242bdfa20f82/3750x3750.png\">\n" +
                    "        </td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table><table class=\"module\" role=\"module\" data-type=\"text\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"948e3f3f-5214-4721-a90e-625a47b1c957\" data-mc-module-version=\"2019-10-22\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td style=\"padding:50px 30px 18px 30px; line-height:36px; text-align:inherit; background-color:#ffffff;\" height=\"100%\" valign=\"top\" bgcolor=\"#ffffff\" role=\"module-content\"><div><div style=\"font-family: inherit; text-align: center\"><span style=\"font-size: 43px\">Thanks for signing up!</span></div><div></div></div></td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table><table class=\"module\" role=\"module\" data-type=\"text\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"a10dcb57-ad22-4f4d-b765-1d427dfddb4e\" data-mc-module-version=\"2019-10-22\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td style=\"padding:18px 30px 18px 30px; line-height:22px; text-align:inherit; background-color:#ffffff;\" height=\"100%\" valign=\"top\" bgcolor=\"#ffffff\" role=\"module-content\"><div><div style=\"font-family: inherit; text-align: center\"><span style=\"font-size: 18px\">Please use the below OTP code to verify your account</span></div>\n" +
                    "<div style=\"font-family: inherit; text-align: center\"><span style=\"font-size: 18px; color: #d40d0d\"><strong>$otpCode</strong></span></div>\n" +
                    "<div style=\"font-family: inherit; text-align: left\"><br></div>\n" +
                    "<div style=\"font-family: inherit; text-align: left\"><span style=\"color: #020202; font-size: 14px\">Regards,</span></div>\n" +
                    "<div style=\"font-family: inherit; text-align: left\"><span style=\"color: #020202; font-size: 14px\">Mayalu Team</span></div><div></div></div></td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table><table class=\"module\" role=\"module\" data-type=\"spacer\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\" data-muid=\"7770fdab-634a-4f62-a277-1c66b2646d8d.1\">\n" +
                    "    <tbody>\n" +
                    "      <tr>\n" +
                    "        <td style=\"padding:0px 0px 50px 0px;\" role=\"module-content\" bgcolor=\"#ffffff\">\n" +
                    "        </td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table></td>\n" +
                    "        </tr>\n" +
                    "      </tbody>\n" +
                    "    </table></td>\n" +
                    "      </tr>\n" +
                    "    </tbody>\n" +
                    "  </table><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"module\" data-role=\"module-button\" data-type=\"button\" role=\"module\" style=\"table-layout:fixed;\" width=\"100%\" data-muid=\"550f60a9-c478-496c-b705-077cf7b1ba9a\">\n" +
                    "      <tbody>\n" +
                    "        <tr>\n" +
                    "          <td align=\"center\" bgcolor=\"\" class=\"outer-td\" style=\"padding:0px 0px 20px 0px;\">\n" +
                    "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"wrapper-mobile\" style=\"text-align:center;\">\n" +
                    "              <tbody>\n" +
                    "                <tr>\n" +
                    "                <td align=\"center\" bgcolor=\"#f5f8fd\" class=\"inner-td\" style=\"border-radius:6px; font-size:16px; text-align:center; background-color:inherit;\"><a href=\"https://sendgrid.com/\" style=\"background-color:#f5f8fd; border:1px solid #f5f8fd; border-color:#f5f8fd; border-radius:25px; border-width:1px; color:#a8b9d5; display:inline-block; font-size:10px; font-weight:normal; letter-spacing:0px; line-height:normal; padding:5px 18px 5px 18px; text-align:center; text-decoration:none; border-style:solid; font-family:helvetica,sans-serif;\" target=\"_blank\">♥ POWERED BY TWILIO SENDGRID</a></td>\n" +
                    "                </tr>\n" +
                    "              </tbody>\n" +
                    "            </table>\n" +
                    "          </td>\n" +
                    "        </tr>\n" +
                    "      </tbody>\n" +
                    "    </table></td>\n" +
                    "                                      </tr>\n" +
                    "                                    </table>\n" +
                    "                                    <!--[if mso]>\n" +
                    "                                  </td>\n" +
                    "                                </tr>\n" +
                    "                              </table>\n" +
                    "                            </center>\n" +
                    "                            <![endif]-->\n" +
                    "                          </td>\n" +
                    "                        </tr>\n" +
                    "                      </table>\n" +
                    "                    </td>\n" +
                    "                  </tr>\n" +
                    "                </table>\n" +
                    "              </td>\n" +
                    "            </tr>\n" +
                    "          </table>\n" +
                    "        </div>\n" +
                    "      </center>\n" +
                    "    </body>\n" +
                    "  </html>"
        }
    }
}