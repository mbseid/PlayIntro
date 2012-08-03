class dashboard
	init: () ->
		$("#postThread").click (event) ->
			jsRoutes.controllers.Transaction.post().ajax
				url: "/post"
				type: "POST"
				context: this
				data:
					accountNumber: "ABC123"
					amount: "2.02"
				success: (data) ->
					false
		false
	message: (msg) ->
	 	messages = msg.split("#")
	 	@handleMessage message for message in messages
	 	false

	handleMessage: (msg) ->
		split = msg.split(":")
		type = split[0]
		switch type
			when "transactions" then @addTransactions(split[1])

		false

	addTransactions: (transactionsString) ->
		if(transactionsString =="")
			return
		transactions = transactionsString.split(";")
		#remove the last element.  Needed because the split leaves the a rouge element
		transactions.pop()
		for t in transactions
			$("#transactions").append("<li>"+t+"</li>")


window.dashboard = new dashboard();