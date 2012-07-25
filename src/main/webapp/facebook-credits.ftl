<!-- Copyright 2004-2011 Facebook. All Rights Reserved.-->

<html xmlns="http://www.w3.org/1999/xhtml"
 xmlns:fb="http://www.facebook.com/2008/fbml">
<head>
  <title>Facebook Credits Demo Application</title>
</head>

<body>
  <h2> Facebook Credits Demo Application</h2>

  <p> Create an order by specifying the following attributes:</br>
  <i>Title, price, description, image URL and product URL</i></p>
  
  <!-- Please note that user can change any information in order_info through 
	javascript. So please make sure you never put price or any other 
	information you don't want users to modify in order_info. We put everything
	here only for end-to-end flow testing purpose!! -->
  <form name ="place_order" id="order_form" action="#">
  Title:       <input type="text" name="title" value="BFF Locket"
                id="title_el"> </br></br>
  Price:       <input type="text" name="price" value="10"
                id="price_el"> </br></br>
  Description: <input type="text" name="description" size="64"
                value="This is a BFF Locket..." id="desc_el"> </br></br>
  Image URL:   <input type="text" name="image_url" size="64"
                value="http://www.facebook.com/images/gifts/21.png"
                id="img_el"> </br></br>
  Product URL: <input type="text" name="product_url" size="64"
                value="http://www.facebook.com/images/gifts/21.png"
                id="product_el"> </br></br>
  <a onclick="placeOrder(); return false;">
    <img src="http://www.facebook.com/connect/button.php?app_id=<?php echo APP_ID; ?>&feature=payments&type=light_l">
  </a>
  </form>


  <div id="output"> ${html} </div> </br></br>

  <a href="facebook-credits">Back to home</a>

  <div id="fb-root"></div>
  <script src="http://connect.facebook.net/en_US/all.js">
  </script>

  <script>
    FB.init({appId: ${appId}, status: true, cookie: true});

    function placeOrder() {
      var title = document.getElementById('title_el').value;
      var desc = document.getElementById('desc_el').value;
      var price = document.getElementById('price_el').value;
      var img_url = document.getElementById('img_el').value;
      var product_url = document.getElementById('product_el').value;

      // Only send param data for sample. These parameters should be set
      // in the callback.
      var order_info = { "title":title,
                         "description":desc,
                         "price":price,
                         "image_url":img_url,
                         "product_url":product_url
                       };

      // calling the API ...
      var obj = {
	    method: 'pay',
	    order_info: order_info,
	    purchase_type: 'item'
      };

      FB.ui(obj, callback);
    }
    
    var callback = function(data) {
      if (data['order_id']) {
        writeback("Transaction Completed! </br></br>"
        + "Data returned from Facebook: </br>"
        + "<b>Order ID: </b>" + data['order_id'] + "</br>"
        + "<b>Status: </b>" + data['status']);
      } else if (data['error_code']) {
        writeback("Transaction Failed! </br></br>"
        + "Error message returned from Facebook:</br>"
        + data['error_message']);
      } else {
        writeback("Transaction failed!");
      }
    };

    function writeback(str) {
      document.getElementById('output').innerHTML=str;
    }

  </script>

</body>
</html>



