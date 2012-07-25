<div id="starterPage">
    <h2>Set Access Token</h2>
    <label for="accessToken">access token:</label>
    <input id="accessToken" type="text" style="width: 500px">


    <h2>Facebook Connect Friends Demo</h2>
    <label for="limit">Friends limit:</label>
    <input id="limit" type="text" style="width: 25px" value="10">
    <label for="offset">offset:</label>
    <input id="offset" type="text" style="width: 25px" value="0">
    <a href="" class="fetchFriends">Fetch Friends</a>

    <div class="friends"></div>
    <script type="text/javascript">
        $(function () {
            $("#starterPage").delegate("a.fetchFriends", "click", function () {
                var token = $("#accessToken").val();
                var limit = parseInt($("#limit").val(), 10) || 10;
                var offset = parseInt($("#offset").val(), 10) || 0;

                if (/^\s*$/g.test(token)) {
                    alert("please set token first");
                } else {
                    $.ajax({
                        type : "get",
                        url : contextPath + "/friends.json",
                        data : {token : token, limit : limit, offset : offset},
                        dataType : "json"
                    }).done(function (val) {
                                $(".friends").html($("#tmpl-FriendsView").render({users : val}));
                            });
                }
                return false;
            });
        });
    </script>

    <div class="search">
        <h2>Public Search Demo</h2>
        <label>Search</label>
        <input type="text" name="q" style="width: 150px">
        <select name="searchType">
            <option value="post">Post</option>
            <option value="user">People</option>
            <option value="page">Pages</option>
            <option value="event">Events</option>
        </select>
        <a href="" name="search">Search</a>


        <script type="text/javascript">
            $(function () {
                $("div.search").delegate("a[name='search']", "click", function () {
                    var token = $("#accessToken").val();
                    var q = $(":input[name='q']").val();
                    var type = $(":input[name='searchType']").val();
                    alert(q + ":" + type);
                    $.ajax({
                        type : "get",
                        url : contextPath + "/search.json",
                        data : {token : token, q : q, type : type},
                        dataType : "json"
                    }).done(function (val) {
                                var vals = [];
                                $.each(val, function(i, obj){
                                     vals.push(JSON.stringify(obj));
                                });
                                $("div.content").html($("#tmpl-SearchView").render({vals : vals}));
                            });

                    return false;
                });
            });
        </script>
        <div class="content">
        </div>
    </div>
</div>

<script id="tmpl-FriendsView" type="text/html">
    <ul>

        {{for users}}
        <li>
            {{:name}}
        </li>
        {{/for}}

    </ul>
</script>

<script id="tmpl-SearchView" type="text/html">
    <ul>
        {{for vals}}
        <li>
            {{:#data}}
        </li>
        {{/for}}

    </ul>
</script>