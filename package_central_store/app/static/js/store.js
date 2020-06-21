window.onload = function(){
    var product_ids =   $.makeArray($("table").map((idx, elem) => {return elem.className}))
                         .map((elem) => {return elem.split("_")[1]});

    console.log("product_ids: " + JSON.stringify(product_ids));
    product_ids.forEach((product_id) => {
        store_1_url = "http://localhost:5001/stocks"
        store_2_url = "http://localhost:5002/stocks"
        //STORE_1 REQUEST
        $.ajax({
            url: store_1_url,
            type: 'post',
            contentType: 'application/xml',
            data: "<?xml version='1.0' encoding='utf-8'?><stocks><productID>" + product_id + "</productID></stocks>",
            processData: false,
            success: function( data, textStatus, jQxhr ){
                $(".product_" + product_id + " .store_1").text(data);
            },
            error: function( jqXhr, textStatus, errorThrown ){
                $(".product_" + product_id + " .store_1").text("X");
                console.log( "FAILED, jqXhr: " + jqXhr );
                console.log( "FAILED, textStatus: " + textStatus );
                console.log( "FAILED, errorThrown: " + errorThrown );
            }
        });

        //STORE_2 REQUEST
        $.ajax({
            url: store_2_url,
            type: 'post',
            contentType: 'application/xml',
            data: "<?xml version='1.0' encoding='utf-8'?><stocks><productID>" + product_id + "</productID></stocks>",
            processData: false,
            success: function( data, textStatus, jQxhr ){
                $(".product_" + product_id + " .store_2").text(data);
            },
            error: function( jqXhr, textStatus, errorThrown ){
                $(".product_" + product_id + " .store_2").text("X");
                console.log( "FAILED, jqXhr: " + jqXhr );
                console.log( "FAILED, textStatus: " + textStatus );
                console.log( "FAILED, errorThrown: " + errorThrown );
            }
        });

    });
};


