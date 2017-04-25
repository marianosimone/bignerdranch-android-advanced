package com.bignerdranch.android.nerdmart;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.android.nerdmart.databinding.ViewProductRowBinding;
import com.bignerdranch.android.nerdmartservice.service.payload.Product;

import java.util.List;

class ProductRecyclerViewAdapter
        extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ProductViewHolder> {

    private Context mContext;
    private AddProductClickEvent mAddProductClickEvent;
    private List<Product> mProducts;

    interface AddProductClickEvent {
        void onProductAddClick(Product product);
    }

    public ProductRecyclerViewAdapter(List<Product> products,
                                      Context context,
                                      AddProductClickEvent addProductClickEvent) {
        mProducts = products;
        mContext = context;
        mAddProductClickEvent = addProductClickEvent;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        ViewProductRowBinding viewProductRowBinding = DataBindingUtil
                .inflate(layoutInflater,
                        R.layout.view_product_row,
                        parent,
                        false);
        return new ProductViewHolder(viewProductRowBinding);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, final int position) {
        Product product = mProducts.get(position);
        holder.bindHolder(product, position);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public void setProducts(List<Product> products) {
        mProducts = products;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ViewProductRowBinding mDataBinding;

        ProductViewHolder(ViewProductRowBinding dataBinding) {
            super(dataBinding.getRoot());
            mDataBinding = dataBinding;
        }

        private void bindHolder(Product product, int position) {
            mDataBinding.viewProductRowTitle.setText(product.toString());
            mDataBinding.setBuyButtonClickListener(v ->
                    mAddProductClickEvent.onProductAddClick(product));
        }
    }
}