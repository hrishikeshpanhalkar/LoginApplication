package com.example.loginapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.loginapplication.R;
import com.example.loginapplication.Model.MainData;

import java.util.ArrayList;
import java.util.List;



public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> implements Filterable {

    Context context;
    ArrayList<MainData> mainData;
    private final List<MainData> filteroutput;
    private final SelectedUser selectedUser;

    public MainAdapter(Context c,ArrayList<MainData> t, SelectedUser selectedUser){
        this.context=c;
        this.mainData=t;
        this.filteroutput=mainData;
        this.selectedUser=selectedUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Name.setText(mainData.get(position).getName());
        holder.Email.setText(mainData.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mainData.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults=new FilterResults();
                if(constraint == null | constraint.length() == 0){
                    filterResults.count=filteroutput.size();
                    filterResults.values=filteroutput;
                }else {
                    String searchchar=constraint.toString().toLowerCase();
                    List<MainData> resultData=new ArrayList<>();
                    for(MainData mainData: filteroutput){
                        if(mainData.getName().toLowerCase().contains(searchchar)){
                            resultData.add(mainData);
                        }
                    }
                    filterResults.count=resultData.size();
                    filterResults.values=resultData;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mainData= (ArrayList<MainData>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public interface SelectedUser{
        void selectedUser(MainData mainData);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Name,Email;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name=(TextView)itemView.findViewById(R.id.textview1);
            Email=(TextView)itemView.findViewById(R.id.textview2);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    selectedUser.selectedUser(mainData.get(getAdapterPosition()));
                }
            });
        }
    }

}