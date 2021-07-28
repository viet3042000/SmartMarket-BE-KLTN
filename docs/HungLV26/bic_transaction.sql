SELECT * FROM (SELECT c.request_id create_id, 
u.request_id update_id,
c.order_reference,
c.order_id order_id,
c.ord_date,
c.consumer_id,
COALESCE(u.customer_name,c.customer_name) AS customer_name,
COALESCE(u.phone_number,c.phone_number) AS phone_number,
COALESCE(u.email,c.email) AS email,
c.product_id,
c.ord_paid_money,
COALESCE(u.customer_address,c.customer_address) AS customer_address,
c.from_date,
c.to_date,
c.result_code create_result,
u.result_code update_result,
COALESCE(u.log_timestamp,c.log_timestamp) AS log_timestamp,
COALESCE(u.destroy,c.destroy) AS destroy
FROM 
	(SELECT * FROM bic_transaction WHERE "type" = 'CREATE' AND result_code='000' order by log_timestamp desc) c
LEFT JOIN 
	(SELECT * FROM bic_transaction WHERE "type" = 'UPDATE' AND result_code='000'order by log_timestamp desc) u
ON (c.order_id = u.order_id) 
AND u.log_timestamp = 
(
   SELECT MAX(log_timestamp) 
   FROM bic_transaction z 
   WHERE z.order_id = u.order_id AND z.result_code = '000'
) 
ORDER BY log_timestamp desc) orders 
WHERE log_timestamp > '2021-07-27 24:00:00.000+07';