
     $(document).ready(function(){
            $("#signin_section").show();         /* it shows the sign in and hide the operation sections */
            $("#operation_section").hide();
            ChangeUrl("Signin","/signin.html");  /* it sets the new url when the page is loaded */
     });

      /* --------  functions  --------- */


       /* function used to get the jwt from cookies after having saved it, for later async ajax calls to microservices */
       function getCookie(name) {
            var value = "; " + document.cookie;
            var parts = value.split("; " + name + "=");
            if (parts.length == 2)
                return parts.pop().split(";").shift();
       }

        /* function used to change the text of the url giving the impression we're changing page */
        function ChangeUrl(title, url) {
            if (typeof (history.pushState) != "undefined") {
               var obj = { Title: title, Url: url };
               history.pushState(obj, obj.Title, obj.Url);
            } else {
               alert("Browser does not support HTML5.");
            }
        }


         /* --------  on click events  -------- */

         /* sign in submit function */
        $("#submit").click(function(e) {
            e.preventDefault();
            $.ajax({                                                        /* Ajax call to AccountMicroservice for login */
               url: 'http://localhost:8094/login',
               type: "POST",
               data: {
                  id: $("#id").val(),
                  password: $("#password").val()
               },
               success: function (data, status, xhr) {
                  $("#signin_section").hide();                              /* it hides sign in section and shows operation section */
                  $("#operation_section").show();
                  $("#showCoupon").hide();
                  $("#operations").hide();
                  console.log(data);
                  ChangeUrl("Operations","/operations.html");               /* it changes url after sign in */

                  document.cookie = "jwt=" + xhr.getResponseHeader("jwt");  /* it saves in cookies the received token */
               },
               error: function(result) {
                  alert("Sign in failed!");
                  console.log(result);
               }
            });
        });



        /* sign out submit function */
        $("#signout").click(function(e) {
            e.preventDefault();
            document.cookie = "jwt=;expires=Thu, 01 Jan 1970 00:00:01 GMT;";        /* it cancels jwt from cookies */
            $("#accountsList").empty();
            $("#operationsTitle").empty();                                          /* it empties all lists and info */
            $("#operationsList").empty();
            $("#operations").hide();
            $("#signin_section").show();                                            /* it hides operations and shows sign in section */
            $("#operation_section").hide();
            alert("You're logged out!");
            ChangeUrl("Signin","/signin.html");                                     /* it changes url again to signin.html */
        });


        /* on click of #getAllAccounts button */
        $("#getAccounts").click(function(e) {
            e.preventDefault();

            /* first Ajax call */
            $.ajax({                                                                /* Ajax call to AccountMicroservice for accouns */
               url: 'http://localhost:8094/accounts/user',
               type: "POST",
               success: function (data, status, xhr) {
                  console.log(data);
                  $("#accountsList").empty();                                       /* Reset info */
                  $("#operationsTitle").empty();
                  $("#operationsList").empty();
                  $("#showCoupon").show();
                  $("#operations").hide();
                  jQuery.each(data.response, function(i, val) {
                     /* with the response it fills #accountList */
                     $("#accountsList").append("<li><h3><a id='" + val.id + "' class='accountButton' title='Go to operations'>Account " + val.id+ "</a></h3></li>");
                  });
               },
               error: function(result) {
                  alert("Error accessing accounts!");
                  console.log(result);
               }
            });

            /* second Ajax call */
            $.ajax({                                                                /* Ajax call to CouponMicroservice for coupons */
               url: 'http://localhost:8095/findCoupon',
               type: "POST",
               data: {
                  jwt: getCookie("jwt")                                             /* it gets jwt from cookies and send as body POST */
               },
               success: function (data, status, xhr) {
                  console.log(data);
                  $("#coupons").empty();
                  $("#coupons").append(data.response);                              /* it resets and fills #coupons section with result */
                  console.log("Data.response: " + data.response);
               },
               error: function(result) {
                  alert("Error accessing CouponMicroservice!");
                  console.log(result);
               }
            });

        });


        /* onclick on the choosen Account */
        //get operation for one account
        $(document).on('click', '.accountButton', function (e) {
            e.preventDefault();
            $("#operationsTitle").text("Operations for account number " + e.target.id);     /* it sets the title of the section */
            $.ajax({                                                                        /* Ajax call to AccountMicroservice */
               url: 'http://localhost:8094/operations/account/' + e.target.id,
               type: "GET",
               success: function (data, status, xhr) {
                  console.log(data);
                  $("#operationsList").empty();
                  jQuery.each(data.response, function(i, val) {
                     /* with the response it fills #operationsList */
                     $("#operationsList").append("<li><h4>COD: " + val.id + ", DESCR: " + val.description + ", VALUE: " + val.value + "</h3></li>");
                  });
               },
               error: function(result) {
                  alert("Error!");
                  console.log(result);
               }
            });
            $("#operations").show();                        /* it shows the sub section of the operations */
            $("#codeInput").val("");                        /* it empties the form to save a new operation */
            $("#descrInput").val("");
            $("#valueInput").val("");
            $("#fkAccount1Input").val(e.target.id);
            $("#fkAccount2Input").val("");
        });

        /* onclick to save a new operation in database */
        $("#submitNewOperation").click(function(e) {
            e.preventDefault();
            if($("#codeInput").val() != "" && $("#valueInput").val() != ""){      /* having a valid value to submit */
               $.ajax({
                  url: 'http://localhost:8094/operations/add',                  /* Ajax call to AccountMicroservice to save operation */
                  type: "POST",
                  data: {
                     id:          $("#codeInput").val(),                        /* it gets the values from the HTML5 form to send in req. */
                     value:       $("#valueInput").val(),
                     description: $("#descrInput").val(),
                     fkAccount1:  $("#fkAccount1Input").val(),
                     fkAccount2:  $("#fkAccount2Input").val()
                  },
                  success: function (data, status, xhr) {
                     alert("Success!");
                     $("#codeInput").val("");                        /* it empties the form to save a new operation */
                     $("#descrInput").val("");
                     $("#valueInput").val("");
                     $("#fkAccount1Input").val(e.target.id);
                     $("#fkAccount2Input").val("");
                     $("#operations").hide();
                  },
                  error: function(result) {
                     alert("Error!");
                     console.log(result);
                  }
               });
            }else{
                alert("Insert a valid operation!");
            }
        });

